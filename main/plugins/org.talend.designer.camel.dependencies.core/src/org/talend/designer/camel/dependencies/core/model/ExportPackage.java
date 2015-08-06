package org.talend.designer.camel.dependencies.core.model;

import org.osgi.framework.Version;

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

    private static String normalizeVersion(String version) {
        Version v = Version.parseVersion(version);
        return v != Version.emptyVersion ? v.toString() : null;
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

	public boolean strictEqual(Object obj) {
		if (!equals(obj)) {
			return false;
		}
		return isEquals(version, ((ExportPackage) obj).getVersion());
	}

	private static boolean isEquals(String a, String b) {
		if (a == null) {
			if (b == null) {
				return true;
			} else {
				return false;
			}
		}

		return a.equals(b);
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

}
