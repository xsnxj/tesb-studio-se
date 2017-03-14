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
package org.talend.designer.esb.runcontainer.ui.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.jface.action.Action;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.designer.esb.runcontainer.ui.dialog.RuntimeInfoDialog;
import org.talend.designer.esb.runcontainer.util.ERunContainerImage;
import org.talend.designer.esb.runcontainer.util.JMXUtil;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class OpenRuntimeInfoAction extends Action {

    public OpenRuntimeInfoAction() {
        setToolTipText("Running Information");
        setImageDescriptor(ImageProvider.getImageDesc(ERunContainerImage.INFO_RUNTIME_ICON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        MBeanServerConnection mbsc = JMXUtil.connectToRuntime();

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            String JOB_MBEAN = "TalendAgent:type=O.S. Informations";
            ObjectName objectJob = new ObjectName(JOB_MBEAN);
            MBeanInfo info = mbsc.getMBeanInfo(objectJob);
            MBeanAttributeInfo[] attrInfo = info.getAttributes();

            for (int i = 0; i < attrInfo.length; i++) {
                try {
                    Map<String, String> attributeMap = new HashMap<String, String>();
                    String attributeName = attrInfo[i].getName();
                    attributeMap.put("name", attributeName);
                    String attributeDesc = attrInfo[i].getType();
                    attributeMap.put("desc", attributeDesc);
                    String attributeValue = mbsc.getAttribute(objectJob, attributeName).toString();
                    attributeMap.put(attributeName, attributeValue);
                    attributeMap.put("value", attributeValue);
                    list.add(attributeMap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RuntimeInfoDialog dlg = new RuntimeInfoDialog(list);
        dlg.open();
    }
}
