package org.talend.designer.camel.dependencies.core.model;

import org.eclipse.osgi.service.resolver.VersionRange;
import org.talend.designer.camel.dependencies.core.util.VersionValidateUtil;

public abstract class OsgiDependencies extends AbstractDependencyItem {

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

	public boolean strictEqual(final OsgiDependencies obj) {
		if (!equals(obj)) {
			return false;
		}
		if (VersionValidateUtil.compare(name, versionRange, obj.name, obj.versionRange, VersionValidateUtil.IMatchRules.EQUIVALENT) 
				&& isOptional == obj.isOptional()) {
			return true;
		}
		return false;
	}


	@Override
	public String getLabel() {
		return name + ' ' + VersionValidateUtil.getVersionLabelString(versionRange);
	}

    @Override
	public String toString() {
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

	abstract protected String getVersionPrefix();
}
