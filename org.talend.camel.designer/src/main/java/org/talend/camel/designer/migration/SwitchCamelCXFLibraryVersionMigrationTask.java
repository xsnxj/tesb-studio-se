// ============================================================================
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

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SwitchCamelCXFLibraryVersionMigrationTask extends AbstractItemMigrationTask {

    private static final ProxyRepositoryFactory FACTORY = ProxyRepositoryFactory.getInstance();

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
        GregorianCalendar gc = new GregorianCalendar(2012, 2, 2, 14, 40, 00);
        return gc.getTime();
    }

    @Override
    public ExecutionResult execute(Item item) {

        try {
            switchVersion(item);
            return ExecutionResult.SUCCESS_NO_ALERT;
        } catch (Exception e) {
            ExceptionHandler.process(e);
            return ExecutionResult.FAILURE;
        }

    }

    private void switchVersion(Item item) throws PersistenceException {
        ProcessType processType = getProcessType(item);
        for (Object o : processType.getNode()) {
            if (o instanceof NodeType) {
                NodeType currentNode = (NodeType) o;
                if ("cMessagingEndpoint".equals(currentNode.getComponentName())) {
                    for (Object e : currentNode.getElementParameter()) {
                        ElementParameterType p = (ElementParameterType) e;
                        if ("HOTLIBS".equals(p.getName())) {
                            EList elementValue = p.getElementValue();
                            for (Object pv : elementValue) {
                                ElementValueType evt = (ElementValueType) pv;
                                String evtValue = evt.getValue();
                                evtValue = switchVersion(evtValue);
                                evt.setValue(evtValue);
                            }
                        }
                    }
                }
            }
        }

        FACTORY.save(item, true);

    }

    /**
     * 
     * DOC LiXP Comment method "switchVersion".
     * 
     * @param evtValue
     * @return
     */
    private String switchVersion(String evtValue) {
        if (evtValue == null) {
            return evtValue;
        }

        String result = "";
        if (evtValue.contains("camel-")) {
            result = evtValue.replaceAll("2.7.\\d", "2.8.2-SNAPSHOT");
            result = result.replaceAll("2\\.8\\.2-SNAPSHOT", "2.8.2");
            result = result.replaceAll("2\\.8\\.2", "2.8.4");
        }
        if (evtValue.contains("spring-")) {
            result = evtValue.replace("3.0.5", "3.0.6");
        }
        if (evtValue.contains("cxf-bundle")) {
            result = evtValue.replaceAll("2.4.\\d", "2.5.0-SNAPSHOT");
            result = result.replaceAll("2\\.5\\.0-SNAPSHOT", "2.5.0");
            result = result.replaceAll("2\\.5\\.0", "2.5.2");
        }
        if (evtValue.contains("activemq-all")) {
            result = evtValue.replaceAll("5.1.0", "5.5.1");
        }
        return result;
    }

}
