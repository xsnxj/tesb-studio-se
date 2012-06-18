package org.talend.designer.camel.dependencies.core.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class OsgiDependencies implements IDependencyItem {
	protected String minVersion = null;
	protected String maxVersion = null;
	protected boolean isOptional = false;
	// import-package name or require-bundle name
	protected String name = null;

	// indicate the dependency is built-in or user added
	private boolean isBuiltIn = false;

	// version regular expression
	protected static final String VERSION_REGEX = "\\d+\\.\\d+\\.\\d(\\.(\\w|-|_)+)?";

	private Pattern versionPattern = Pattern.compile(VERSION_REGEX);

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = normalizeVersion(minVersion);
	}

	public String getMinVersion() {
		return minVersion;
	}

	public void setMaxVersion(String maxVersion) {
		this.maxVersion = normalizeVersion(maxVersion);
	}

	public String getMaxVersion() {
		return maxVersion;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public void setBuiltIn(boolean isBuiltIn) {
		this.isBuiltIn = isBuiltIn;
	}

	public boolean isBuiltIn() {
		return isBuiltIn;
	}

	/**
	 * only care about the name, ignore others
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ImportPackage)) {
			return false;
		}
		if (name == null) {
			return false;
		}
		return name.equals(((ImportPackage) obj).getName());
	}

	/**
	 * only care about name
	 */
	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	/**
	 * normalize the version to "*.*.*" if it is not.
	 * 
	 * @param version
	 * @return
	 */
	protected String normalizeVersion(String version) {
		if (version == null)
			return null;
		int dotCount = 0;
		int length = version.length();
		for (int i = 0; i < length; i++) {
			if ('.' == version.charAt(i)) {
				dotCount++;
			}
		}
		dotCount = 2 - dotCount;
		if (dotCount == 0) {
			return version;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(version);
		for (int i = 0; i < dotCount; i++) {
			sb.append(".0");
		}
		return sb.toString();
	}

	public static final int OK = 0;
	public static final int NAME_NULL = 1;
	public static final int MIN_INVALID = 2;
	public static final int MAX_INVALID = 4;
	public static final int MIN_MAX_INVALID = 8;

	/**
	 * validate the dependency information is valid or not
	 * 
	 * @return {@link #OK} {@link #NAME_NULL} {@link #MIN_INVALID}
	 *         {@link #MAX_INVALID} {@link #MIN_MAX_INVALID}
	 */
	public int isValid() {
		if (name == null) {
			return NAME_NULL;
		}
		if (minVersion != null) {
			Matcher matcher = versionPattern.matcher(minVersion);
			if (!matcher.matches()) {
				return MIN_INVALID;
			}
		}

		if (maxVersion != null) {
			Matcher matcher = versionPattern.matcher(maxVersion);
			if (!matcher.matches()) {
				return MAX_INVALID;
			}
		}

		if (!compareMinMax()) {
			return MIN_MAX_INVALID;
		}
		return OK;
	}

	private boolean compareMinMax() {
		if (maxVersion == null || minVersion == null) {
			return true;
		}
		String[] maxSplit = maxVersion.split("\\.");
		String[] minSplit = minVersion.split("\\.");
		for (int i = 0; i < 3; i++) {
			try {
				if (Integer.parseInt(maxSplit[i])
						- Integer.parseInt(minSplit[i]) < 0) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (minVersion != null && maxVersion != null) {
			sb.append("[");
			sb.append(minVersion);
			sb.append(",");
			sb.append(maxVersion);
			sb.append(")");
		} else if (minVersion != null) {
			sb.append("(");
			sb.append(minVersion);
			sb.append(")");
		} else if (maxVersion != null) {
			sb.append("(");
			sb.append(maxVersion);
			sb.append(")");
		}
		return sb.toString();
	}
}
