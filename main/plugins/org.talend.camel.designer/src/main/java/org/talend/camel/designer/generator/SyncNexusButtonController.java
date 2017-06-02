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
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Button;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.core.utils.TalendQuoteUtils;

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

            if(needUpdateJars!=null && needUpdateJars.size()==0){
                MessageDialog.openInformation(composite.getShell(), "Synchronizing libraries", "Everything is up-to-date");
            }else{
                for (int i = 0; i < needUpdateJars.size(); i++) {
                    Map<String,Object> jar = (Map)needUpdateJars.get(i);
                    
                    String jn = TalendQuoteUtils.removeQuotes(jar.get("JAR_NAME").toString());
                    String jnv = TalendQuoteUtils.removeQuotes(jar.get("JAR_NEXUS_VERSION").toString());
                    String a = jn.replaceFirst("[.][^.]+$", "");
                    
                    if(Boolean.valueOf(jar.get("JAR_SYNC").toString())){
                        if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibrariesService.class)) {
                            ILibrariesService librariesService = (ILibrariesService) GlobalServiceRegister.getDefault().getService(
                                    ILibrariesService.class);
                            
                            InputStream is = service.getContentInputStream(nexusServerBean, "", getGroupId(), a, jnv, null);
                            
                            File file = generateTempFile(is, jn);
                            
                            try {
                                
                                librariesService.deployLibrary(file.toURI().toURL(), "mvn:org.talend.libraries/"+a+"/"+jnv+"/jar");
                                
                                jar.put("JAR_STATUS", "✔");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
				
            refresh(needUpdateList, true);
                
			

            
            return null;
        }
        return null;
    }
    
    private void deploy(File file, String version){
		
        try {
            if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibrariesService.class)) {
                ILibrariesService service = (ILibrariesService) GlobalServiceRegister.getDefault().getService(
                        ILibrariesService.class);
                
                service.deployLibrary(file.toURI().toURL(), version);
            }
        } catch (IOException ee) {
            ExceptionHandler.process(ee);
        }
    }

}
