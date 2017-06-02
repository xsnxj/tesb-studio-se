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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.runtime.maven.MavenArtifact;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.maven.utils.PomUtil;
import org.talend.librariesmanager.prefs.LibrariesManagerUtils;
import org.talend.utils.io.FilesUtils;
import org.talend.utils.string.MD5;

public class CheckNexusButtonController extends ConfigOptionController {
	
    private static Logger log = Logger.getLogger(CheckNexusButtonController.class);
    
    public CheckNexusButtonController(IDynamicProperty dp) {
        super(dp);
    }

    public Command createCommand(Button button) {
        
        if(!isAvailable()){
            return null;
        }
        
        IElementParameter parameter = (IElementParameter) button.getData();
        if (parameter != null) {
            callBeforeActive(parameter);
            // so as to invoke listeners to perform some actions.
    		
    		IElementParameter elementParameterFromField = elem.getElementParameter("DRIVER_JAR");
    		
    		List needUpdateJars = null;
    		
    		List jars = (List) elementParameterFromField.getValue();
    		
    		if(jars.size()>0){
    			
    			IElementParameter needUpdateList = elem.getElementParameter("NEED_UPDATE_LIST");
    			
    			needUpdateJars = (List) needUpdateList.getValue();
    			
    			needUpdateJars.clear();
    			
				List<ModuleNeeded> updatedModules = null;
				
				if (elem instanceof Node) {
					updatedModules = LibrariesManagerUtils.getNotInstalledModules(((Node) elem));
				}
			    
			    if(nexusServerBean == null){
			        return null;
			    }
				
    			for (int i = 0; i < jars.size(); i++) {
    				Map<String,String> jar = (Map)jars.get(i);
    				
    				String currentNexusVersion = TalendQuoteUtils.removeQuotes(jar.get(JAR_NEXUS_VERSION));
    				String currentNexusPreVersion = jar.get(JAR_NEXUS_PRE_VERSION);
					String jn = TalendQuoteUtils.removeQuotes(jar.get(JAR_NAME));
    				String a = jn.replaceFirst("[.][^.]+$", "");
    				
    				try {
                        
                        Map metadata = service.getMavenMetadata(nexusServerBean, getGroupId(), a, currentNexusVersion);
                        
                        if(metadata.get("Versioning.Latest").equals(currentNexusVersion)){
                            
                            MavenArtifact ma = new MavenArtifact();
                            ma.setArtifactId(a);
                            ma.setGroupId("org.talend.libraries");
                            ma.setVersion(currentNexusVersion);
                            ma.setType("jar");
                            
                            String p = PomUtil.getAbsArtifactPath(ma);
                            
                            InputStream is = service.getContentInputStream(nexusServerBean, "", getGroupId(), a, metadata.get("Versioning.Latest").toString(), "jar.md5");
                            
                            if(p != null){
                                
                                if(is != null){
                                    String remoteM2FileMD5 = "";
                                    try (BufferedReader buffer = new BufferedReader(new InputStreamReader(is))) {
                                        remoteM2FileMD5 =  buffer.lines().collect(Collectors.joining("\n"));
                                    }
                                    File f = new File(p);//local file
                                    
                                    if(f.exists()){
                                        String localM2FileMD5 = MD5.getMD5(FilesUtils.getBytes(f));
                                        
                                        if(!StringUtils.equalsIgnoreCase(localM2FileMD5, remoteM2FileMD5)){
                                            needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
                                        }
                                    }else{
                                        needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
                                    }
                                }
                            }else{
                                if(is != null){
                                    needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,currentNexusPreVersion));
                                }
                            }
                            
                        }else{
                            needUpdateJars.add(getNeedUpdateJar("✘",jn,currentNexusVersion,metadata.get("Versioning.Latest").toString()));
                        }
                        

    				} catch (Exception ee) {
    					ee.printStackTrace();
    				}
    			}
    			
				refresh(needUpdateList, true);
    		}
    		
    		if(needUpdateJars!=null && needUpdateJars.size()==0){
    		    MessageDialog.openInformation(composite.getShell(), "Checking libraries", "Everything is up-to-date");
    		}

    		return null;
        }
        return null;
    }
}
