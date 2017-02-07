package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class RenameToCSoapAndCRestMigrationTask extends AbstractRouteItemComponentMigrationTask {

	private static final String C_CXF = "cCXF";
    private static final String C_CXFRS = "cCXFRS";
    private static final String C_SOAP = "cSOAP";
    private static final String C_REST = "cREST";


	@Override
	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2017, 2, 1, 0, 0, 0);
		return gc.getTime();
	}

	@Override
	public String getComponentNameRegex() {
		return "cCXF|cCXFRS";

	}

	 @Override
	    protected boolean execute(NodeType node) throws Exception {
	        boolean needSave = false;
	        if (node.getComponentName().equals(C_CXF)) {
	            needSave = handleRenameCXFNode(node);
	        } else if (node.getComponentName().equals(C_CXFRS)) {
	            needSave = handleRenameCXFRSNode(node);
	        }
	        return needSave;
	    }
	 /**
		 * Rename node cCXF to cSOAP 
		 * @param node CXFNode node.
		 *
		 * @return true, if handle rename node
		 */

	    private boolean handleRenameCXFNode(NodeType node) {
	        node.setComponentName(C_SOAP);
	        ElementParameterType param = UtilTool.findParameterType(node, "UNIQUE_NAME");
	        UtilTool.replaceValueSubSequence(param, C_CXF, C_SOAP);
	        return true;
	    }
	    /**
		 * Rename node cCXFRS to cREST 
		 * @param node CXFRSNode node.
		 *
		 * @return true, if handle rename node
		 */

	    private boolean handleRenameCXFRSNode(NodeType node) {
	        node.setComponentName(C_REST);
	        ElementParameterType param = UtilTool.findParameterType(node, "UNIQUE_NAME");
	        UtilTool.replaceValueSubSequence(param, C_CXFRS, C_REST);
	        return true;
	    }

}
