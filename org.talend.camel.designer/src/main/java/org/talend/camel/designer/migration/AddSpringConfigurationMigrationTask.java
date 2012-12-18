package org.talend.camel.designer.migration;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.camel.designer.util.CamelSpringUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.model.migration.AbstractItemMigrationTask;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;

/**
 * used to set the default spring configuration content
 * when importing an old version route item
 */
public class AddSpringConfigurationMigrationTask extends
		AbstractItemMigrationTask {

	private static final ProxyRepositoryFactory FACTORY = ProxyRepositoryFactory
			.getInstance();

	@Override
	public List<ERepositoryObjectType> getTypes() {
		List<ERepositoryObjectType> toReturn = new ArrayList<ERepositoryObjectType>();
		toReturn.add(CamelRepositoryNodeType.repositoryRoutesType);
		return toReturn;
	}

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 12, 17, 14, 00, 00);
		return gc.getTime();
	}

	@Override
	public ExecutionResult execute(Item item) {

		try {
			if (item instanceof CamelProcessItem) {
				addDefaultSpringContentFor((CamelProcessItem) item);
			}
			return ExecutionResult.SUCCESS_NO_ALERT;
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}

	}

	private void addDefaultSpringContentFor(CamelProcessItem item)
			throws PersistenceException {
		String defaultContent = CamelSpringUtil.getDefaultContent(item);
		item.setSpringContent(defaultContent);
		FACTORY.save(item, true);
	}
}