package org.talend.camel.designer.ui;

import java.util.List;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.talend.designer.core.model.components.EmfComponent;
import org.talend.designer.core.ui.editor.nodes.Node;
import org.talend.designer.core.ui.editor.nodes.NodePart;

public class OpenTalendJobRefAction extends org.talend.designer.core.ui.editor.CustomExternalActions {

    private static final String REQUEST_TYPE_OPEN_NODE_PART = "open";
    private static final String C_TALEND_JOB_COMPONENT_NAME = "cTalendJob";
    private static final String ORG_TALEND_RCP_INTEGRATION_PERSPECTIVE = "org.talend.rcp.perspective";

    public OpenTalendJobRefAction() {
    }

    @Override
    public int getComponentType() {
        return 0;
    }

    @Override
    protected boolean calculateEnabled() {
        List<?> selectedObjects = getSelectedObjects();
        if (selectedObjects.size() != 1) {
            return false;
        }
        Object select = selectedObjects.get(0);
        if (!(select instanceof NodePart)) {
            return false;
        }

        NodePart nodePart = (NodePart) select;
        Node node = (Node) nodePart.getModel();
        if (!(node.getComponent() instanceof EmfComponent)) {
            return false;
        }
        EmfComponent component = (EmfComponent) node.getComponent();
        if (!C_TALEND_JOB_COMPONENT_NAME.equals(component.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        // open in editor, type and count already checked in calculateEnabled()
        List<?> selectedObjects = getSelectedObjects();
        Object select = selectedObjects.get(0);
        NodePart nodePart = (NodePart) select;
        nodePart.performRequest(new org.eclipse.gef.Request(REQUEST_TYPE_OPEN_NODE_PART));

        // switch perspective.
        try {
            PlatformUI.getWorkbench().showPerspective(ORG_TALEND_RCP_INTEGRATION_PERSPECTIVE, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
        } catch (WorkbenchException e) {
            e.printStackTrace();
        }
    }

}
