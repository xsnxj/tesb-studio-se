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
package org.talend.camel.designer.ui.editor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.talend.camel.designer.ui.SaveAsRoutesAction;
import org.talend.camel.designer.ui.action.RoutePasteAction;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.PluginChecker;
import org.talend.core.model.components.IComponentsHandler;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.process.INode;
import org.talend.core.nexus.NexusServerBean;
import org.talend.core.nexus.TalendLibsServerManager;
import org.talend.core.runtime.maven.MavenArtifact;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.core.ui.editor.AbstractTalendEditor;
import org.talend.designer.maven.utils.PomUtil;
import org.talend.repository.ui.wizards.exportjob.util.ExportJobUtil;
import org.talend.utils.io.FilesUtils;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelTalendEditor extends AbstractTalendEditor {

    private Map<String,String> cConfigStoredInfo = null;
    
    private static CamelComponentsHandler CAMEL_COMPONENTS_HANDLER;

    public CamelTalendEditor() {
        super();
    }

    public CamelTalendEditor(boolean readOnly) {
        super(readOnly);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initializeGraphicalViewer() {
        super.initializeGraphicalViewer();

        // Set DND listener by CamelEditorDropTargetListener
        getGraphicalViewer().removeDropTargetListener(talendEditorDropTargetListener);
        talendEditorDropTargetListener.setEditor(null);
        talendEditorDropTargetListener = null;
        talendEditorDropTargetListener = new CamelEditorDropTargetListener(this);
        talendEditorDropTargetListener.setEditor(this);
        getGraphicalViewer().addDropTargetListener(talendEditorDropTargetListener);

        IAction pasteAction = new RoutePasteAction(this);
        getActionRegistry().registerAction(pasteAction);
        
        List<? extends INode> graphicalNodes = this.getProcess().getGraphicalNodes();
        
        cConfigStoredInfo = new HashMap<>();
        
        for (INode node : graphicalNodes) {
            if (node.getComponent().getName().equals("cConfig")){
                List<Map<String,String>> jars = (List) node.getElementParameter("DRIVER_JAR").getValue();
                
                for(Map<String,String> o:jars){
                    String jn = TalendQuoteUtils.removeQuotes(o.get("JAR_NAME"));
                    String jnv = TalendQuoteUtils.removeQuotes(o.get("JAR_NEXUS_VERSION"));
                    
                    cConfigStoredInfo.put(jn, jnv);
                }
            }
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        super.doSave(monitor);
        
        if (!PluginChecker.isTIS()) {
            return;
        }
        
        NexusServerBean nexusServerBean = TalendLibsServerManager.getInstance().getCustomNexusServer();
        
        if(nexusServerBean == null){
            return;
        }
        
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ILibrariesService.class)) {
            ILibrariesService service = (ILibrariesService) GlobalServiceRegister.getDefault().getService(
                    ILibrariesService.class);
        
            List<? extends INode> graphicalNodes = this.getProcess().getGraphicalNodes();
            for (INode node : graphicalNodes) {
                if (node.getComponent().getName().equals("cConfig")){
                    List<Map<String,String>> jars = (List) node.getElementParameter("DRIVER_JAR").getValue();
                    
                    for(Map<String,String> o:jars){
                        
                        String jn = TalendQuoteUtils.removeQuotes(o.get("JAR_NAME"));
                        String jnv = TalendQuoteUtils.removeQuotes(o.get("JAR_NEXUS_VERSION"));
                        String jv = String.valueOf(o.get("JAR_PATH"));
                        String a = jn.replaceFirst("[.][^.]+$", "");
                        
                        if(StringUtils.isNotBlank(jv)){
                            File jarFile = new File(jv);
                            
                            if(jarFile.exists()){
                                
                                try {
                                    service.deployLibrary(jarFile.toURI().toURL(), "mvn:org.talend.libraries/"+a+"/"+jnv+"/jar");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                cConfigStoredInfo.put(jn, jnv);
                                o.put("JAR_PATH","");
                            }
                        }
                        
                        if(cConfigStoredInfo.get(jn) == null){
                            cConfigStoredInfo.put(jn, jnv);
                            continue;
                        }
                        
                        if(cConfigStoredInfo.get(jn).equals(jnv)){
                            continue;
                        }else{     
                            MavenArtifact ma = new MavenArtifact();
                            ma.setArtifactId(a);
                            ma.setGroupId("org.talend.libraries");
                            ma.setVersion(cConfigStoredInfo.get(jn));
                            ma.setType("jar");
                            
                            String p = PomUtil.getAbsArtifactPath(ma);
                            
                            if(p != null){
                                File file = new File(p);
                                try {
                                    if(file.exists()){
                                        File tmp = new File(ExportJobUtil.getTmpFolder() + File.separator + jn);
                                        
                                        FilesUtils.copyFile(file, tmp);
                                        
                                        service.deployLibrary(tmp.toURI().toURL(), "mvn:org.talend.libraries/"+a+"/"+jnv+"/jar");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    
    }
    
    @Override
    public void doSaveAs() {
        SaveAsRoutesAction saveAsAction = new SaveAsRoutesAction(this.getParent());
        saveAsAction.run();
    }

    protected IComponentsHandler initComponentsHandler() {
        if (CAMEL_COMPONENTS_HANDLER == null) {
            synchronized (CamelTalendEditor.class) {
                CAMEL_COMPONENTS_HANDLER = new CamelComponentsHandler();
            }
        }
        return CAMEL_COMPONENTS_HANDLER;
    }

}
