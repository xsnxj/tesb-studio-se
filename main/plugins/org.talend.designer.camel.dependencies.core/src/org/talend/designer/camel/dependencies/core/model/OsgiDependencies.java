package org.talend.designer.camel.dependencies.core.model;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.talend.designer.camel.dependencies.core.util.VersionValidateUtil;

public abstract class OsgiDependencies<T extends OsgiDependencies<?>> extends
		AbstractDependencyItem {
	protected boolean isOptional = false;
	private String versionRange;

	public String getVersionRange() {
		return versionRange;
	}

	public void setVersionRange(String versionRange) {
		try {
			//not parse empty range.
			if (!(new VersionRange(versionRange).equals(VersionRange.emptyRange))) {
				this.versionRange = versionRange;
			}else {
				this.versionRange=null;
			}
		} catch (Exception e) {
			//version format illegal.
			this.versionRange=null;
		}
	}

	public OsgiDependencies() {
	}

	public OsgiDependencies(String inputString) {
		try {
			parse(inputString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OsgiDependencies(T copied) {
		if (copied != null) {
			setName(copied.getName());
			setOptional(copied.isOptional());
			setVersionRange(copied.getVersionRange());
		}
	}

	private void parse(String inputString) {
		String[] split = inputString.split(";"); //$NON-NLS-1$
		setName(split[0]);
		if (split.length <= 1) {
			return;
		}
		for (int i = 1; i < split.length; i++) {
			String s = split[i];
			if ("resolution:=optional".equals(s)) { //$NON-NLS-1$
				setOptional(true);
			} else if (s.startsWith(getVersionPrefix())) { //$NON-NLS-1$
				parseVersions(s);
			}
		}
	}
	
	private void parseVersions(String input) {
		int firstQuote = input.indexOf("\""); //$NON-NLS-1$
		int lastQuote = input.lastIndexOf("\""); //$NON-NLS-1$
		VersionRange versionRange = new VersionRange(input.substring(
				firstQuote + 1, lastQuote));
		setVersionRange(versionRange.toString());
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
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
		if (obj.getClass() != getClass()) {
			return false;
		}
		if (name == null) {
			return false;
		}
		return name.equals(((OsgiDependencies<?>) obj).getName());
	}

	@Override
	public boolean strictEqual(Object obj) {
		if (!equals(obj)) {
			return false;
		}
		OsgiDependencies<?> o = (OsgiDependencies<?>) obj;
		
		if (VersionValidateUtil.compare(name, versionRange, o.name, o.versionRange, VersionValidateUtil.IMatchRules.EQUIVALENT) 
				&& isOptional == o.isOptional()) {
			return true;
		}
		return false;
	}

	protected boolean isEquals(String a, String b) {
		if (a == null) {
			if (b == null) {
				return true;
			} else {
				return false;
			}
		}

		return a.equals(b);
	}

	/**
	 * only care about name
	 */
	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	/**
	 * validate the dependency information is valid or not
	 * 
	 * @return {@link #OK} {@link #NAME_NULL} {@link #MIN_INVALID}
	 *         {@link #MAX_INVALID} {@link #MIN_MAX_INVALID}
	 */
	public int isValid() {
		return OK;
	}

	@Override
	public String getLabel() {
		return name + " "
				+ VersionValidateUtil.getVersionLabelString(versionRange);
	}

	public String toManifestString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (versionRange != null) {
			sb.append(";");
			sb.append(getVersionPrefix());
			sb.append("=\"");
			sb.append(versionRange);
			sb.append("\"");
		}

		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return toManifestString();
	}

	abstract protected String getVersionPrefix();
}
