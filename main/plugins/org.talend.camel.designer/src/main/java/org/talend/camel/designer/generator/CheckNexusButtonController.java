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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.widgets.Button;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.core.utils.TalendQuoteUtils;
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
    				String currentNexusVersion = TalendQuoteUtils.removeQuotes(jar.get(JAR_NEXUS_VERSION));
    				String currentNexusPreVersion = jar.get(JAR_NEXUS_PRE_VERSION);
					String jn = TalendQuoteUtils.removeQuotes(jar.get(JAR_NAME));
    				String a = jn.replaceFirst("[.][^.]+$", "");
    				
    				try {
    				    
    		        	Map metadata = service.getMavenMetadata(getGroupId(), a , currentNexusVersion);
    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
    		        	String release = (String) metadata.get("Versioning.Release");
    		        	
    		        	if(StringUtils.isNotBlank(updated)){
        					long current = Long.MAX_VALUE;
        					
        					if(StringUtils.isBlank(currentVersion)){
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
        					
        					if( !StringUtils.equals(currentNexusVersion, currentNexusPreVersion) ){
        						needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
        						continue;
        					}
        					
        					if(sdf.parse(updated).getTime()>current){
        						if(StringUtils.isNotBlank(release)){
        							if(StringUtils.equals(currentNexusVersion, release)){
        								needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
        							}
        						}else{
            						needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
        						}
        						continue;
        					}
    		        	}
    		            
    				} catch (Exception ee) {
    					ee.printStackTrace();
    				}
    			}
    			
				refresh(needUpdateList, true);
    		}

    		return null;
        }
        return null;
    }
}
