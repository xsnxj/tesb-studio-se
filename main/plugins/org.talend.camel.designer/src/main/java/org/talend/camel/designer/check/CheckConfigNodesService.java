package org.talend.camel.designer.check;

import org.talend.core.PluginChecker;
import org.talend.core.nexus.NexusServerBean;
import org.talend.core.nexus.TalendLibsServerManager;
import org.talend.designer.core.ICheckNodesService;
import org.talend.designer.core.ui.editor.nodes.Node;

public class CheckConfigNodesService implements ICheckNodesService{

	@Override
	public void checkNode(Node node) {
		if (!PluginChecker.isTIS() || !node.getComponent().getName().equals("cConfig")) { //$NON-NLS-1$
			return;
		}
		
		NexusServerBean nexusServerBean = TalendLibsServerManager.getInstance().getCustomNexusServer();
		
		if(nexusServerBean == null){
		    return;
		}
	}
}
