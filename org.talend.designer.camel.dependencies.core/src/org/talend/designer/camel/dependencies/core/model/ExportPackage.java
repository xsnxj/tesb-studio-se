package org.talend.designer.camel.dependencies.core.model;

import java.util.regex.Matcher;

public class ExportPackage extends AbstractDependencyItem {

	private String version = null;

	public ExportPackage() {
	}

	public ExportPackage(String input) {
		parse(input);
	}

	public ExportPackage(ExportPackage ep) {
		if (ep != null) {
			this.name = ep.getName();
			this.version = ep.getVersion();
		}
	}

	private void parse(String input) {
		if (input == null) {
			return;
		}
		String[] split = input.split(";"); //$NON-NLS-1$
		if (split.length < 1) {
			return;
		}
		setName(split[0]);
		if (split.length > 1) {
			int firstQuote = input.indexOf("\""); //$NON-NLS-1$
			int lastQuote = input.lastIndexOf("\""); //$NON-NLS-1$
			setVersion(input.substring(firstQuote + 1, lastQuote));
		}
	}

	public void setVersion(String version) {
		this.version = normalizeVersion(version);
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (version != null) {
			sb.append(" ("); //$NON-NLS-1$
			sb.append(version);
			sb.append(")"); //$NON-NLS-1$
		}
		return sb.toString();
	}

	@Override
	public int getType() {
		return EXPORT_PACKAGE;
	}

	@Override
	public boolean strictEqual(Object obj) {
		if (!equals(obj)) {
			return false;
		}
		return isEquals(version, ((ExportPackage) obj).getVersion());
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
		return name.equals(((ExportPackage) obj).getName());
	}

	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);

		if (version != null) {
			sb.append(";version=\""); //$NON-NLS-1$
			sb.append(version);
			sb.append("\""); //$NON-NLS-1$
		}

		return sb.toString();
	}

	public static final int VERSION_INVALID = 4;

	public int isValid() {
		if (name == null || name.trim().equals("")) { //$NON-NLS-1$
			return NAME_NULL;
		}

		if (!namePattern.matcher(name).matches()) {
			return NAME_INVALID;
		}

		if (version != null && !version.trim().equals("")) { //$NON-NLS-1$
			Matcher matcher = versionPattern.matcher(version);
			if (!matcher.matches()) {
				return VERSION_INVALID;
			}
		}

		return OK;
	}
}
