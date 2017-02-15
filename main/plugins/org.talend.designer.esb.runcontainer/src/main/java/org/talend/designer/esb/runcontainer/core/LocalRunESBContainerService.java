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
package org.talend.designer.esb.runcontainer.core;

import org.eclipse.swt.widgets.Composite;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.process.EComponentCategory;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.designer.esb.runcontainer.process.ESBRunContainerProcessContext;
import org.talend.designer.esb.runcontainer.ui.RunESBContainerComposite;
import org.talend.designer.runprocess.IESBRunContainerService;
import org.talend.designer.runprocess.RunProcessContext;
import org.talend.designer.runprocess.ui.views.ProcessView;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class LocalRunESBContainerService implements IESBRunContainerService {

    private RunESBContainerComposite esbContainerComposite;

    private RunProcessContext containerProcessContext;

    private RunProcessContext esbProcessContext;

    @Override
    public EComponentCategory getRunCategory() {
        return EComponentCategory.ESBCONTAINERRUN;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IESBRunContainerService#hasContainer()
     */
    @Override
    public boolean hasRuntimeContainer(String componentCategory) {
        return componentCategory.equals(ComponentCategory.CATEGORY_4_CAMEL.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.runprocess.IESBRunContainerService#createRunContainerComposite(org.eclipse.swt.widgets.Composite
     * , org.talend.designer.runprocess.RunProcessContext, int)
     */
    @Override
    public IDynamicProperty createRunContainerComposite(ProcessView viewPart, Composite parent, RunProcessContext processContext,
            int style) {
        esbContainerComposite = new RunESBContainerComposite(viewPart, parent, processContext, style);
        if (containerProcessContext != null) {
            esbContainerComposite.setProcessContext(containerProcessContext);
        }
        return esbContainerComposite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IESBRunContainerService#setRunProcessContext(org.talend.designer.runprocess.
     * RunProcessContext)
     */
    @Override
    public void setRunProcessContext(RunProcessContext context) {
        containerProcessContext = context;
        // if (esbContainerComposite != null) {
        // esbContainerComposite.setProcessContext(containerProcessContext);
        // }

        if (esbContainerComposite != null) {
            if (esbProcessContext == null) {
                esbProcessContext = new ESBRunContainerProcessContext(containerProcessContext.getProcess());
            }
            esbContainerComposite.setProcessContext(esbProcessContext);
        }
    }

}
