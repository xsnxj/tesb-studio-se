// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.dependencies.core.util;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;

/**
 * The Class VersionValidateUtil. Use for validate version string.
 */
public class VersionValidateUtil {

	/**
	 * Try to get valid version range.
	 * 
	 * @param minV
	 *            the min v
	 * @param maxV
	 *            the max v
	 * @return the string
	 */
	public static String tryToGetValidVersionRange(String minV, String maxV) {
		return tryToGetValidVersionRange(minV, maxV, true, false);
	}

	/**
	 * Try to get valid version range.
	 * 
	 * @param minV
	 *            the min v
	 * @param maxV
	 *            the max v
	 * @param minI
	 *            the min i
	 * @param maxI
	 *            the max i
	 * @return the string
	 */
	public static String tryToGetValidVersionRange(String minV, String maxV,
			boolean minI, boolean maxI) {
		if (minV == null) {
			return null;
		}
		if (minV.equals(maxV)) {
			minI = maxI = true;
		}
		return new VersionRange(minV == null ? Version.emptyVersion
				: new Version(minV), minI, maxV == null ? Version.emptyVersion
				: new Version(maxV), maxI).toString();
	}

}
