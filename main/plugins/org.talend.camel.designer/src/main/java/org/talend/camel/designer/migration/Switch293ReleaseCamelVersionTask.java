// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.migration;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author xpli
 *
 */
public class Switch293ReleaseCamelVersionTask extends CMessgingEndpointSwitchVersionTask {

	public Date getOrder() {
		GregorianCalendar gc = new GregorianCalendar(2012, 4, 19, 14, 00, 00);
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
			result = evtValue.replace("2.9.2", "2.9.3");
		}
		if (evtValue.startsWith("cxf-bundle")) {
			result = evtValue.replace("2.6.0", "2.6.2");
		}
		if (evtValue.startsWith("spring-")) {
			result = evtValue.replace("3.0.6", "3.0.7");
		}
		return result;
	}

}
