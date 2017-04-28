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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Button;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.core.ui.editor.cmd.PropertyChangeCommand;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;
import org.talend.librariesmanager.utils.RemoteModulesHelper;
import org.talend.repository.ui.wizards.exportjob.util.ExportJobUtil;
import org.talend.utils.io.FilesUtils;

public class SyncNexusButtonController extends ConfigOptionController {

    public SyncNexusButtonController(IDynamicProperty dp) {
        super(dp);
    }

    public Command createCommand(Button button) {
        IElementParameter parameter = (IElementParameter) button.getData();
        if (parameter != null) {
            callBeforeActive(parameter);
            // so as to invoke listeners to perform some actions.
    		
    		IElementParameter elementParameterFromField = elem.getElementParameter("DRIVER_JAR");
			IElementParameter needUpdateList = elem.getElementParameter("NEED_UPDATE_LIST");
    		
			List needUpdateJars = (List) needUpdateList.getValue();
			
			needUpdateJars.clear();
			
    		List jars = (List) elementParameterFromField.getValue();
    		
			List<ModuleNeeded> updatedModules = null;
			
			if (elem instanceof Node) {
				updatedModules = LibrariesManagerUtils.getNotInstalledModules(((Node) elem));
			}
		
			try {
				if(updatedModules!=null){
					for(ModuleNeeded moduleNeeded:updatedModules){
						String mn = moduleNeeded.getModuleName();
						String a = TalendQuoteUtils.removeQuotes(mn).replaceFirst("[.][^.]+$", "");
						
						for(int i=0;i<jars.size();i++){
							
							Map<String,String> jar = (Map)jars.get(i);
							String jn = TalendQuoteUtils.removeQuotes(jar.get(JAR_NAME));
							if(jn.equals(mn)){
								
								String nexusVersion = TalendQuoteUtils.removeQuotes(jar.get(JAR_NEXUS_VERSION));
								String nexusPreVersion = jar.get(JAR_NEXUS_PRE_VERSION);
								
		    		        	Map metadata = service.getMavenMetadata(null, a, nexusVersion);
		    		        	
		    		        	if(metadata != null){
			    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
									
			    		        	if(StringUtils.isNotBlank(updated)){
		        						Map u = executeUpdate(jar,"✔", mn,a,updated, nexusVersion, nexusPreVersion);
		        						
		        						if(u != null){
		        							needUpdateJars.add(u);
		        						}
										
										break;
			    		        	}
		    		        	}
							}
						}
						
						IRunnableWithProgress runnable = RemoteModulesHelper.getInstance().getNotInstalledModulesRunnable(updatedModules, new ArrayList(), true);
						try {
							runnable.run(new NullProgressMonitor());
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						continue;
					}
					
					refresh(needUpdateList, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		if(jars.size()>0){
    			
    			boolean shouldUpdate = false;
    			
    			for (int i = 0; i < jars.size(); i++) {
    				Map<String,String> jar = (Map)jars.get(i);
    				
    				String currentVersion = jar.get(JAR_VERSION);
					String currentNexusVersion = TalendQuoteUtils.removeQuotes(jar.get(JAR_NEXUS_VERSION));
					String currentNexusPreVersion = jar.get(JAR_NEXUS_PRE_VERSION);
    				String jn = TalendQuoteUtils.removeQuotes(jar.get(JAR_NAME));
    				String a = jn.replaceFirst("[.][^.]+$", "");

    				try {
    		        	Map metadata = service.getMavenMetadata(null, a , currentNexusVersion);
    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
    		        	String release = (String) metadata.get("Versioning.Release");
    					
    					if(StringUtils.isNotBlank(updated)){
    						long current = Long.MAX_VALUE;
    						
    	   					if(StringUtils.isBlank(currentVersion)){
        						current = 0;
        					}else{
        						current = sdf.parse(currentVersion).getTime();
        					}
    	   					
        					if( !StringUtils.equals(currentNexusVersion, currentNexusPreVersion) ){
        						Map u = executeUpdate(jar,"✔", jn,a,updated, currentNexusVersion, currentNexusPreVersion);
        						
        						if(u != null){
        							shouldUpdate = true;
        							needUpdateJars.add(u);
        						}
        						continue;
        					}
        					
        					if(sdf.parse(updated).getTime()>current){
        						if(StringUtils.isNotBlank(release)){
        							if(StringUtils.equals(currentNexusVersion, release)){
                						Map u = executeUpdate(jar,"✔", jn,a,updated, currentNexusVersion, currentNexusPreVersion);
                						
                						if(u != null){
                							shouldUpdate = true;
                							needUpdateJars.add(u);
                						}
        							}
        						}else{
            						Map u = executeUpdate(jar,"✔", jn,a,updated, currentNexusVersion, currentNexusPreVersion);
            						
            						if(u != null){
            							shouldUpdate = true;
            							needUpdateJars.add(u);
            						}
        						}
        						continue;
        					}
    					}
    				} catch (Exception ee) {
    					ee.printStackTrace();
    				}
    			}
    			
    			if(shouldUpdate){
        			refresh(needUpdateList, true);
        			return new PropertyChangeCommand(elem, parameter.getName(), null);
    			}
    		}
            
            return null;
        }
        return null;
    }
    
    public Map executeUpdate(Map update, String flag,String jarName,String artifact,String version,String nexusVersion,String nexusPreVersion){
    	
		InputStream is = service.getContentInputStream(null, null, artifact, nexusVersion);
		
		if(is != null){
			update.put(JAR_VERSION,version);
			update.put(JAR_NEXUS_VERSION,update.get(JAR_NEXUS_VERSION));
			update.put(JAR_NEXUS_PRE_VERSION,nexusVersion);
			
			deploy(generateTempFile(is,jarName));
			
			return getNeedUpdateJar(flag, jarName, nexusVersion, nexusPreVersion);
		}
		return null;
    }
    
    private File generateTempFile(InputStream is,String fileName){
    	File file = new File(ExportJobUtil.getTmpFolder()+File.separator+fileName);

		try {
			FilesUtils.copyFile(is, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
    }
    
    private void deploy(File file){
		
        try {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibrariesService.class)) {
                ILibrariesService service = (ILibrariesService) GlobalServiceRegister.getDefault().getService(
                        ILibrariesService.class);
                
                service.deployLibrary(file.toURI().toURL());
            }
        } catch (IOException ee) {
            ExceptionHandler.process(ee);
        }
    }

}
