package org.talend.camel.designer.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

public class CSetHeaderSupportMultiHeadersTask extends
		AbstractItemMigrationTask {

	private static final ProxyRepositoryFactory FACTORY = ProxyRepositoryFactory
			.getInstance();

	@Override
	public List<ERepositoryObjectType> getTypes() {
		List<ERepositoryObjectType> toReturn = new ArrayList<ERepositoryObjectType>();
		toReturn.add(CamelRepositoryNodeType.repositoryRoutesType);
		return toReturn;
	}

	public ProcessType getProcessType(Item item) {
		if (item instanceof ProcessItem) {
			return ((ProcessItem) item).getProcess();
		}
		return null;
	}

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 9, 10, 14, 00, 00);
		return gc.getTime();
	}

	@Override
	public ExecutionResult execute(Item item) {

		try {
			cSetHeaderMigrate(item);
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}

	}

	private void cSetHeaderMigrate(Item item) throws PersistenceException {
		ProcessType processType = getProcessType(item);
		for (Object o : processType.getNode()) {
			if (o instanceof NodeType) {
				NodeType currentNode = (NodeType) o;
				if (!"cSetHeader".equals(currentNode.getComponentName())) {
					continue;
				}
				EList parameters = currentNode.getElementParameter();
				String headerName = null;
				String useBean = null;
				String expression = null;
				String language = null;
				String bean = null;
				for (Object tmp : parameters) {
					ElementParameterType param = (ElementParameterType) tmp;
					String paramName = param.getName();
					if ("HEADER".equals(paramName)) {
						headerName = param.getValue();
					} else if ("USE_BEAN".equals(paramName)) {
						useBean = param.getValue();
					} else if ("BEAN".equals(paramName)) {
						bean = param.getValue();
					} else if ("LANGUAGES".equals(paramName)) {
						language = param.getValue();
					} else if ("EXPRESSION".equals(paramName)) {
						expression = param.getValue();
					}
				}

				// setName
				ElementValueType newNameValue = TalendFileFactory.eINSTANCE
						.createElementValueType();
				newNameValue.setElementRef("NAME");
				newNameValue.setValue(headerName);

				// set LANGUAGE
				ElementValueType newLanguageValue = TalendFileFactory.eINSTANCE
						.createElementValueType();
				newLanguageValue.setElementRef("LANGUAGE");
				if ("true".equals(useBean)) {
					newLanguageValue.setValue("bean");
				} else {
					newLanguageValue.setValue(language);
				}

				// set EXPRESSION
				ElementValueType newExpressionValue = TalendFileFactory.eINSTANCE
						.createElementValueType();
				newExpressionValue.setElementRef("EXPRESSION");
				if ("true".equals(useBean)) {
					newExpressionValue.setValue(bean);
				} else {
					newExpressionValue.setValue(expression);
				}

				ElementParameterType newParameter = TalendFileFactory.eINSTANCE
						.createElementParameterType();
				newParameter.setName("VALUES");
				newParameter.getElementValue().add(newNameValue);
				newParameter.getElementValue().add(newLanguageValue);
				newParameter.getElementValue().add(newExpressionValue);

				parameters.add(newParameter);
			}
		}

		FACTORY.save(item, true);

	}
}