package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.emf.common.util.EList;
import org.talend.commons.exception.PersistenceException;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;

public class CSetHeaderSupportMultiHeadersTask extends
		AbstractRouteItemComponentMigrationTask{
	
	@Override
	public String getComponentNameRegex() {
		return "cSetHeader";
	}

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 9, 10, 14, 00, 00);
		return gc.getTime();
	}

	@Override
	protected boolean execute(NodeType node) throws Exception {
		cSetHeaderMigrate(node);
		return true;
	}

	private void cSetHeaderMigrate(NodeType currentNode) throws PersistenceException {
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