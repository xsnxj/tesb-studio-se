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
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Button;
import org.osgi.framework.Bundle;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.properties.tab.IDynamicProperty;
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
    		List jars = (List) elementParameterFromField.getValue();
    		
			List<ModuleNeeded> updatedModules = null;
			
			if (elem instanceof Node) {
				updatedModules = LibrariesManagerUtils.getNotInstalledModules(((Node) elem));
			}
		
			try {
				if(updatedModules!=null){
					for(ModuleNeeded moduleNeeded:updatedModules){
						String mn = moduleNeeded.getModuleName();
						String a = mn.replaceFirst("[.][^.]+$", "");
						
    		        	Map metadata = service.getMavenMetadata(null, a, null);
    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
						
						if(metadata!=null){
							
							InputStream is = service.getContentInputStream(null, null, a, null);
							
							deploy(generateTempFile(is,mn));
							
							for(int i=0;i<jars.size();i++){
								Map<String,String> jar = (Map)jars.get(i);
								if(jar.get(JAR_NAME).equals(mn)){
									jar.put(JAR_VERSION, updated);
									break;
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
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    		
    		if(jars.size()>0){
    			
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");//20170411034415
    			
    			IElementParameter needUpdateList = elem.getElementParameter("NEED_UPDATE_LIST");
    			
    			List needUpdateJars = (List) needUpdateList.getValue();
    			
    			needUpdateJars.clear();
    			
    			for (int i = 0; i < jars.size(); i++) {
    				
    				Map<String,String> jar = (Map)jars.get(i);
    				
    				String currentVersion = jar.get(JAR_VERSION);
    				
    				String jn = jar.get(JAR_NAME);
    				
    				String a = jn.replaceFirst("[.][^.]+$", "");

    				try {
    					
    		        	Map metadata = service.getMavenMetadata(null, a , null);
    		        	String updated = (String) metadata.get("Versioning.LastUpdated");
    					
    					long current = Long.MAX_VALUE;
    					
    					if(StringUtils.isBlank(currentVersion) && StringUtils.isNotBlank(updated)){
    						current = 0;
    					}else{
    						current = sdf.parse(currentVersion).getTime();
    					}
    					
    					if(sdf.parse(updated).getTime()>current){
    						
    						jar.put(JAR_VERSION,updated);

    						refresh(needUpdateList, true);
    						
    	        			InputStream is = service.getContentInputStream(null, null, a, null);
    	        			
    	        			deploy(generateTempFile(is,jar.get(JAR_NAME)));
    	        			
    	        			return new PropertyChangeCommand(elem, parameter.getName(), null);
    					}
    					
    				} catch (Exception ee) {
    					ee.printStackTrace();
    				}

    			}
    		}
    		
            
            return null;//new PropertyChangeCommand(elem, parameter.getName(), null);
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
