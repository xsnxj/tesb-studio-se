package org.talend.repository.view.esb.doc;

import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.core.repository.IExtendRepositoryNode;
import org.talend.repository.model.RepositoryNode;

public class RouteDocumentRepositoryNode implements IExtendRepositoryNode {

	public RouteDocumentRepositoryNode() {
	}

	   /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getNodeImage()
     */

    public IImage getNodeImage() {
        return ECoreImage.FOLDER_OPEN_ICON;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getOrdinal()
     */

    public int getOrdinal() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.repository.IExtendRepositoryNode#getChildren()
     */

    public Object[] getChildren() {
        return new RepositoryNode[0];
    }
}
