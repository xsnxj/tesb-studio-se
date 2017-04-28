package org.talend.camel.designer.check;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.INexusService;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.ui.editor.nodes.Node;

public class CheckConfigNodesService implements ICheckNodesService{

	@Override
	public void checkNode(Node node) {
		if (!node.getComponent().getName().equals("cConfig")) { //$NON-NLS-1$
			return;
		}
		
		IElementParameter v = node.getElementParameter("DRIVER_JAR");
		
		List jars = (List) v.getValue();
		
		if(jars.size()>0){
			for (int i = 0; i < jars.size(); i++) {
				Map<String,String> jar = (Map)jars.get(i);
				
				File file = new File(jar.get("JAR_VERSION"));
				
				if(file.exists()){
			        if (GlobalServiceRegister.getDefault().isServiceRegistered(INexusService.class)) {
			        	INexusService service = (INexusService) GlobalServiceRegister.getDefault().getService(
			        			INexusService.class);
			            Map metadata = null;
						try {
							String nv = TalendQuoteUtils.removeQuotes(jar.get("JAR_NEXUS_VERSION"));
							metadata = service.upload(nv,file.toURI().toURL());
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
			            if(metadata!=null){
			            	jar.put("JAR_NEXUS_PRE_VERSION", jar.get("JAR_NEXUS_VERSION"));
			            	jar.put("JAR_VERSION", (String) metadata.get("Versioning.LastUpdated"));
			            }
			            break;
			        }
				}
			}
		}
	}

}
