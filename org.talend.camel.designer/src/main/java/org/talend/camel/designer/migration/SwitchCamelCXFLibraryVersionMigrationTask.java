// ============================================================================
package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SwitchCamelCXFLibraryVersionMigrationTask extends
CMessgingEndpointSwitchVersionTask {

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2011, 11, 3, 14, 42, 00);
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
			result = evtValue.replaceAll("2.7.\\d", "2.8.2-SNAPSHOT");
			result = result.replaceAll("2\\.8\\.2-SNAPSHOT", "2.8.2");
		}
		if (evtValue.startsWith("spring-")) {
			result = evtValue.replace("3.0.5", "3.0.6");
		}
		if (evtValue.startsWith("cxf-bundle")) {
			result = evtValue.replaceAll("2.4.\\d", "2.5.0-SNAPSHOT");
			result = result.replaceAll("2\\.5\\.0-SNAPSHOT", "2.5.0");
		}
		if (evtValue.startsWith("activemq-all")) {
			result = evtValue.replaceAll("5.1.0", "5.5.1");
		}
		return result;
	}

}
