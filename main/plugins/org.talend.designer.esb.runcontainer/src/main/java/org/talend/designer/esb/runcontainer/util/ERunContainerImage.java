// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.util;

import org.talend.commons.ui.runtime.image.IImage;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public enum ERunContainerImage implements IImage {

    RESTART_RUNTIME_ICON("/icons/restart_runtime.gif"), //$NON-NLS-1$
    START_RUNTIME_ICON("/icons/start_runtime.gif"), //$NON-NLS-1$
    STOP_RUNTIME_ICON("/icons/stop_runtime.gif"), //$NON-NLS-1$
    INFO_RUNTIME_ICON("/icons/info_runtime.gif"); //$NON-NLS-1$

    private String path;

    ERunContainerImage(String path) {
        this.path = path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.ui.runtime.image.IImage#getLocation()
     */
    @Override
    public Class getLocation() {
        // TODO Auto-generated method stub
        return ERunContainerImage.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.commons.ui.runtime.image.IImage#getPath()
     */
    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return this.path;
    }

}
