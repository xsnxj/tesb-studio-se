package org.talend.designer.camel.dependencies.core.model;

import org.talend.designer.camel.dependencies.core.util.VersionValidateUtil;

public abstract class OsgiDependencies<T extends OsgiDependencies<?>> extends
		AbstractDependencyItem {
	protected boolean isOptional = false;
	private String versionRange;

	public String getVersionRange() {
		return versionRange;
	}

	public void setVersionRange(String versionRange) {
		this.versionRange = versionRange;
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

	protected abstract void parse(String inputString);

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
		if (versionRange.equals(o.versionRange)&& isOptional == o.isOptional()) {
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

		return name
				+ " "
				+ (versionRange == null ? "" : VersionValidateUtil
						.getVersionString(versionRange));
	}

	public String toManifestString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(";bundle-version=\"");
		sb.append(VersionValidateUtil.getVersionString(versionRange));
		sb.append("\"");

		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
		}
		return sb.toString();
	}

	abstract protected String getVersionPrefix();
}
