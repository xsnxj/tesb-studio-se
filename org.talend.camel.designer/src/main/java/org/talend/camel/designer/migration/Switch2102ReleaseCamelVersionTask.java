// ============================================================================
package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DOC GangLiu class global comment. Detailled comment
 */
public class Switch2102ReleaseCamelVersionTask extends
		CMessgingEndpointSwitchVersionTask {

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 10, 8, 14, 00, 00);
		return gc.getTime();
	}

	/**
	 * 
	 * DOC LiXP Comment method "switchVersion".
	 * 
	 * @param evtValue
	 * @param properties
	 * @return
	 */
	protected String switchVersion(String evtValue) {
		if (evtValue == null) {
			return evtValue;
		}

		String result = "";
		if (evtValue.startsWith("camel-")) {
			result = evtValue.replace("2.9.3", "2.10.2");
		}
		if (evtValue.startsWith("cxf-bundle")) {
			result = evtValue.replace("2.6.2", "2.7.0");
		}
		if (evtValue.startsWith("activemq-all") || evtValue.startsWith("activemq-pool")) {
			result = evtValue.replaceAll("5.5.1", "5.7.0");
		}
		return result;
	}

}
