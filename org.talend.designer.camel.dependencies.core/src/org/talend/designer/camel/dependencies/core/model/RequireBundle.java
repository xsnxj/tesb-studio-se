package org.talend.designer.camel.dependencies.core.model;

import org.eclipse.osgi.service.resolver.VersionRange;

public class RequireBundle extends OsgiDependencies<RequireBundle> {

	public RequireBundle() {
		super();
	}

	public RequireBundle(String inputString) {
		super(inputString);
	}

	public RequireBundle(RequireBundle copied) {
		super(copied);
	}

	@Override
	protected void parse(String inputString) {

		String[] split = inputString.split(";"); //$NON-NLS-1$
		setName(split[0]);
		if (split.length <= 1) {
			return;
		}
		for (int i = 1; i < split.length; i++) {
			String s = split[i];
			if ("resolution:=optional".equals(s)) { //$NON-NLS-1$
				setOptional(true);
			} else if (s.startsWith("bundle-version=")) { //$NON-NLS-1$
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(";" + getVersionPrefix() + "=\"");
		sb.append(getVersionRange());
		sb.append("\"");

		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	@Override
	protected String getVersionPrefix() {
		return "bundle-version";
	}

	@Override
	public int getType() {
		return REQUIRE_BUNDLE;
	}

//	@Deprecated
//	public void setMaxVersion(String maxVersion) {
//		versionRange = new VersionRange(versionRange.getMinimum(),
//				versionRange.getIncludeMinimum(), new Version(maxVersion),
//				versionRange.getIncludeMaximum());
//	}
//
//	@Deprecated
//	public void setMinVersion(String minVersion) {
//		versionRange = new VersionRange( new Version(minVersion),
//				versionRange.getIncludeMinimum(),versionRange.getMaximum(),
//				versionRange.getIncludeMaximum());
//				
//	}
	
	

}
