//============================================================================
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
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
//============================================================================
package org.talend.designer.esb.runcontainer.ui.console;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IConsoleDocumentPartitioner;
import org.eclipse.ui.console.TextConsole;


/**
 * DOC yyi  class global comment. Detailled comment
 * <br/>
 *
 * $Id$
 *
 */
public class ESBRunContainerConsole extends TextConsole{



    /**
     * DOC yyi ESBRunContainerConsole constructor comment.
     * @param name
     * @param consoleType
     * @param imageDescriptor
     * @param autoLifecycle
     */
    public ESBRunContainerConsole(String name, String consoleType, ImageDescriptor imageDescriptor, boolean autoLifecycle) {
        super(name, consoleType, imageDescriptor, autoLifecycle);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.TextConsole#getPartitioner()
     */
    @Override
    protected IConsoleDocumentPartitioner getPartitioner() {
        // TODO Auto-generated method stub
        return null;
    }
    
    

}
