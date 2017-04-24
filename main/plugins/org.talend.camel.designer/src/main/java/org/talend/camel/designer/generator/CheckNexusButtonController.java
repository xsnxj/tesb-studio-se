// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.generator;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Button;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;

public class CheckNexusButtonController extends ConfigOptionController {
	
    public CheckNexusButtonController(IDynamicProperty dp) {
        super(dp);
    }

    public Command createCommand(Button button) {
        IElementParameter parameter = (IElementParameter) button.getData();
        if (parameter != null) {
            callBeforeActive(parameter);
            // so as to invoke listeners to perform some actions.
    		
    		IElementParameter elementParameterFromField = elem.getElementParameter("DRIVER_JAR");
    		
    		List jars = (List) elementParameterFromField.getValue();
    		
    		if(jars.size()>0){
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");//20170411034415
    			
    			IElementParameter needUpdateList = elem.getElementParameter("NEED_UPDATE_LIST");
    			
    			List needUpdateJars = (List) needUpdateList.getValue();
    			
    			needUpdateJars.clear();
    			
				List<ModuleNeeded> updatedModules = null;
				
				if (elem instanceof Node) {
					updatedModules = LibrariesManagerUtils.getNotInstalledModules(((Node) elem));
				}
    			
    			for (int i = 0; i < jars.size(); i++) {
    				Map<String,String> jar = (Map)jars.get(i);
    				
    				String currentVersion = jar.get(JAR_VERSION);

    				try {

    					String jn = jar.get(JAR_NAME);
    					
    		        	Map metadata = service.getMavenMetadata(null, jn.replaceFirst("[.][^.]+$", ""), null);
    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
    		            
    					long current = Long.MAX_VALUE;
    					
    					if(StringUtils.isBlank(currentVersion) && StringUtils.isNotBlank(updated)){
    						current = 0;
    					}else{
    						current = sdf.parse(currentVersion).getTime();
    					}
    					
    					for(ModuleNeeded moduleNeeded:updatedModules){
    						String mn = moduleNeeded.getModuleName();
    						
    						if(mn.equals(jn)){
    							current = 0;
    							break;
    						}
    					}
    					
    					if(sdf.parse(updated).getTime()>current){
    						Map needUpdateJar = new HashMap();
    						
    						needUpdateJar.put(JAR_NAME, jar.get(JAR_NAME));
    						
    						needUpdateJars.add(needUpdateJar);

    						refresh(needUpdateList, true);
    					}
    				} catch (Exception ee) {
    					ee.printStackTrace();
    				}
    			}
    		}

    		return null;
        }
        return null;
    }

}
