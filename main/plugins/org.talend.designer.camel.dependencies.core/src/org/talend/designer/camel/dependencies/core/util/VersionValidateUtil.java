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
package org.talend.designer.camel.dependencies.core.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Version;
import org.talend.designer.camel.dependencies.core.CoreActivator;
import org.talend.designer.camel.dependencies.core.Messages;

/**
 * The Class VersionValidateUtil. Use for validate version string.
 */
public class VersionValidateUtil {

	/**
	 * Validate version.
	 * 
	 * @param versionString
	 *            the version string
	 * @return the i status
	 */
	public static IStatus validateVersion(String versionString) {
		try {
			if (versionString != null)
				new Version(versionString.trim());
		} catch (IllegalArgumentException e) {
			return new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID,
					IStatus.ERROR,
					Messages.VersionValidateUtil_InvalidFormatInBundleVersion,
					e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Validate version range.
	 * 
	 * @param versionRangeString
	 *            the version range string
	 * @return the i status
	 */
	public static IStatus validateVersionRange(String versionRangeString) {
		try {
			new VersionRange(versionRangeString);
		} catch (IllegalArgumentException e) {
			return new Status(IStatus.ERROR, CoreActivator.PLUGIN_ID,
					IStatus.ERROR,
					Messages.VersionValidateUtil_invalidVersionRangeFormat, e);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Compare.
	 * 
	 * @param id1
	 *            the id1
	 * @param version1
	 *            the version1
	 * @param id2
	 *            the id2
	 * @param version2
	 *            the version2
	 * @param match
	 *            the match
	 * @return true, if successful
	 */
	public static boolean compare(String id1, String version1, String id2,
			String version2, int match) {
		if (!(id1.equals(id2)))
			return false;
		try {
			Version v1 = Version.parseVersion(version1);
			Version v2 = Version.parseVersion(version2);

			switch (match) {
			case IMatchRules.NONE:
			case IMatchRules.COMPATIBLE:
				return isCompatibleWith(v1, v2);
			case IMatchRules.EQUIVALENT:
				return isEquivalentTo(v1, v2);
			case IMatchRules.PERFECT:
				return v1.equals(v2);
			case IMatchRules.GREATER_OR_EQUAL:
				return isGreaterOrEqualTo(v1, v2);
			}
		} catch (RuntimeException e) {
		}
		return version1.equals(version2);
	}

	/**
	 * Returns true if the given version number is an empty version as defined
	 * by {@link Version}. Used in cases where it would be inappropriate to
	 * parse the actual version number.
	 * 
	 * @param version
	 *            version string to check
	 * @return true if empty version
	 */
	public static boolean isEmptyVersion(String version) {
		if (version == null)
			return true;
		version = version.trim();
		return version.length() == 0
				|| version.equals(Version.emptyVersion.toString());
	}

	/**
	 * Checks if is compatible with.
	 * 
	 * @param v1
	 *            the v1
	 * @param v2
	 *            the v2
	 * @return true, if is compatible with
	 */
	public static boolean isCompatibleWith(Version v1, Version v2) {
		if (v1.getMajor() != v2.getMajor())
			return false;
		if (v1.getMinor() > v2.getMinor())
			return true;
		if (v1.getMinor() < v2.getMinor())
			return false;
		if (v1.getMicro() > v2.getMicro())
			return true;
		if (v1.getMicro() < v2.getMicro())
			return false;
		return v1.getQualifier().compareTo(v2.getQualifier()) >= 0;
	}

	/**
	 * Checks if is equivalent to.
	 * 
	 * @param v1
	 *            the v1
	 * @param v2
	 *            the v2
	 * @return true, if is equivalent to
	 */
	public static boolean isEquivalentTo(Version v1, Version v2) {
		if (v1.getMajor() != v2.getMajor() || v1.getMinor() != v2.getMinor())
			return false;
		if (v1.getMicro() > v2.getMicro())
			return true;
		if (v1.getMicro() < v2.getMicro())
			return false;
		return v1.getQualifier().compareTo(v2.getQualifier()) >= 0;
	}

	/**
	 * Checks if is greater or equal to.
	 * 
	 * @param v1
	 *            the v1
	 * @param v2
	 *            the v2
	 * @return true, if is greater or equal to
	 */
	public static boolean isGreaterOrEqualTo(Version v1, Version v2) {
		if (v1.getMajor() > v2.getMajor())
			return true;
		if (v1.getMajor() == v2.getMajor()) {
			if (v1.getMinor() > v2.getMinor())
				return true;
			if (v1.getMinor() == v2.getMinor()) {
				if (v1.getMicro() > v2.getMicro())
					return true;
				if (v1.getMicro() == v2.getMicro())
					return v1.getQualifier().compareTo(v2.getQualifier()) >= 0;
			}
		}
		return false;
	}

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
			return Version.emptyVersion.toString();
		}
		if (minV.equals(maxV)) {
			minI = maxI = true;
		}
		return new VersionRange(minV == null ? Version.emptyVersion
				: new Version(minV), minI, maxV == null ? Version.emptyVersion
				: new Version(maxV), maxI).toString();
	}

	/**
	 * Compare macro minor micro.
	 * 
	 * @param v1
	 *            the v1
	 * @param v2
	 *            the v2
	 * @return the int
	 */
	public static int compareMacroMinorMicro(Version v1, Version v2) {
		int result = v1.getMajor() - v2.getMajor();
		if (result != 0)
			return result;

		result = v1.getMinor() - v2.getMinor();
		if (result != 0)
			return result;

		result = v1.getMicro() - v2.getMicro();
		return result;
	}

	/**
	 * Compute initial plugin version.
	 * 
	 * @param version
	 *            the version
	 * @return the string
	 */
	public static String computeInitialPluginVersion(String version) {
		if (version != null
				&& VersionValidateUtil.validateVersion(version).isOK()) {
			Version pvi = Version.parseVersion(version);
			return pvi.getMajor() + "." + pvi.getMinor() //$NON-NLS-1$
					+ "." + pvi.getMicro(); //$NON-NLS-1$
		}

		return version;
	}

	/**
	 * The Interface IMatchRules.
	 */
	public interface IMatchRules {
		/**
		 * No rule.
		 */
		int NONE = 0;

		/**
		 * A match that is equivalent to the required version.
		 */
		int EQUIVALENT = 1;

		/**
		 * Attribute value for the 'equivalent' rule.
		 */
		String RULE_EQUIVALENT = "equivalent"; //$NON-NLS-1$
		/**
		 * A match that is compatible with the required version.
		 */
		int COMPATIBLE = 2;
		/**
		 * Attribute value for the 'compatible' rule.
		 */
		String RULE_COMPATIBLE = "compatible"; //$NON-NLS-1$
		/**
		 * An perfect match.
		 */
		int PERFECT = 3;

		/**
		 * Attribute value for the 'perfect' rule.
		 */
		String RULE_PERFECT = "perfect"; //$NON-NLS-1$
		/**
		 * A match requires that a version is greater or equal to the specified
		 * version.
		 */
		int GREATER_OR_EQUAL = 4;

		/** Attribute value for the 'greater or equal' rule. */
		String RULE_GREATER_OR_EQUAL = "greaterOrEqual"; //$NON-NLS-1$
		/**
		 * An id match requires that the specified id is a prefix of a candidate
		 * id.
		 */
		int PREFIX = 5;

		/** Attribute value for the 'prefix' id rule. */
		String RULE_PREFIX = "prefix"; //$NON-NLS-1$
		/**
		 * Table of rule names that match rule values defined in this interface.
		 * It can be used directly against the rule values used in plug-in
		 * models.
		 */
		String[] RULE_NAME_TABLE = {
				"", RULE_EQUIVALENT, RULE_COMPATIBLE, RULE_PERFECT, RULE_GREATER_OR_EQUAL }; //$NON-NLS-1$
	}

	/**
	 * Gets the version string.
	 * 
	 * @param versionRange
	 *            the version range
	 * @return the version string
	 */
	public static String getVersionLabelString(String versionRange) {
		if (versionRange == null) {
			return "";
		}
		try {
			if (new VersionRange(versionRange).equals(VersionRange.emptyRange)) {
				return "";
			}
		} catch (Exception e) {
			// illegal version
			e.printStackTrace();
			return "";
		}
		if (versionRange.matches("[^\\(\\)\\[\\]]+")) {
			return "(" + versionRange + ")";
		}
		return versionRange;
	}

}
