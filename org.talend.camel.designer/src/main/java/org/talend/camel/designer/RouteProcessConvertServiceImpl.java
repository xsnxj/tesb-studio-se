package org.talend.camel.designer;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.JobletProcessItem;
import org.talend.designer.core.IProcessConvertService;

public class RouteProcessConvertServiceImpl implements IProcessConvertService {

    public RouteProcessConvertServiceImpl() {
        // TODO Auto-generated constructor stub
    }

    public IProcess getProcessFromItem(Item item, boolean loadScreenshots) {
        if (CamelProcessItem.class.equals(item.getClass())) {
            RouteProcess process = null;
            process = new RouteProcess(item.getProperty());
            return process;
        }
        return null;
    }

}
