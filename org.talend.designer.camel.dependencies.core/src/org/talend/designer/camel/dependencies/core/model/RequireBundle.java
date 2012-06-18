package org.talend.designer.camel.dependencies.core.model;

public class RequireBundle extends OsgiDependencies {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);

		if (minVersion != null && maxVersion != null) {
			sb.append(";bundle-version=\"[");
			sb.append(minVersion);
			sb.append(",");
			sb.append(maxVersion);
			sb.append(")\"");
		} else if (minVersion != null) {
			sb.append(";bundle-version=\"");
			sb.append(minVersion);
			sb.append("\"");
		} else if (maxVersion != null) {
			sb.append(";bundle-version=\"");
			sb.append(maxVersion);
			sb.append("\"");
		}

		if (isOptional) {
			sb.append(";resolution:=optional");
		}

		return sb.toString();
	}

	@Override
	public int getType() {
		return REQUIRE_BUNDLE;
	}

	// public static void main(String[] args) {
	// RequireBundle requireBundle = new RequireBundle();
	// requireBundle.setName("org.eclipse.core.runtime");
	// System.out.println(requireBundle);
	//
	// requireBundle.setMaxVersion("3.6.1");
	// System.out.println(requireBundle);
	//
	// requireBundle.setOptional(true);
	// System.out.println(requireBundle);
	//
	// requireBundle.setMinVersion("1.0");
	// System.out.println(requireBundle);
	// }
}
