package org.talend.camel.designer;

import org.talend.camel.core.model.camelProperties.impl.CamelProcessItemImpl;
import org.talend.camel.designer.ui.editor.RouteProcess;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.designer.core.IProcessConvertService;

public class RouteProcessConvertServiceImpl implements IProcessConvertService {

    public RouteProcessConvertServiceImpl() {
        // TODO Auto-generated constructor stub
    }

    public IProcess getProcessFromItem(Item item, boolean loadScreenshots) {
        if (CamelProcessItemImpl.class == item.getClass()) {
            RouteProcess process = null;
            process = new RouteProcess(item.getProperty());
            process.loadXmlFile(loadScreenshots);
            return process;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IProcessConvertService#doConvert(org.talend.core.model.properties.Item,
     * org.talend.core.model.repository.IRepositoryViewObject)
     */
    public Item doConvert(Item item, IRepositoryViewObject repViewObject) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IProcessConvertService#isOriginalItemDeleted()
     */
    public boolean isOriginalItemDeleted() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.core.IProcessConvertService#isNewItemCreated()
     */
    public boolean isNewItemCreated() {
        return false;
    }

}
