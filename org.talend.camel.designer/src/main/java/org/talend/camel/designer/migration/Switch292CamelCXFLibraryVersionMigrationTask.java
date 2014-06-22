// ============================================================================
package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class Switch292CamelCXFLibraryVersionMigrationTask extends
CMessgingEndpointSwitchVersionTask {

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 3, 8, 14, 00, 00);
		return gc.getTime();
	}

	/**
	 * 
	 * DOC LiXP Comment method "switchVersion".
	 * 
	 * @param evtValue
	 * @return
	 */
	protected String switchVersion(String evtValue) {
		if (evtValue == null) {
			return evtValue;
		}

		String result = "";
		if (evtValue.startsWith("camel-")) {
			result = evtValue.replaceAll("2\\.8\\.\\d", "2.9.2-SNAPSHOT");
		}
		if (evtValue.startsWith("cxf-bundle")) {
			result = evtValue.replaceAll("2\\.5\\.\\d", "2.6.0-SNAPSHOT");
		}
		return result;
	}

}
