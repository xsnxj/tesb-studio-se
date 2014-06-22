package org.talend.designer.camel.dependencies.core.model;


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
		if(split.length<=1){
			return;
		}
		for(int i = 1;i<split.length;i++){
			String s = split[i];
			if("resolution:=optional".equals(s)){ //$NON-NLS-1$
				setOptional(true);
			}else if(s.startsWith("bundle-version=")){ //$NON-NLS-1$
				parseVersions(s);
			}
		}
	}
	
	private void parseVersions(String input) {
		int firstQuote = input.indexOf("\""); //$NON-NLS-1$
		int lastQuote = input.lastIndexOf("\""); //$NON-NLS-1$
		int commaIndex = input.indexOf(","); //$NON-NLS-1$
		if(commaIndex == -1){
			setMinVersion(input.substring(firstQuote+1, lastQuote));
		}else{
			setMinVersion(input.substring(firstQuote+2, commaIndex));
			setMaxVersion(input.substring(commaIndex+1, lastQuote-1));
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);

		if (minVersion != null && maxVersion != null) {
			sb.append(";bundle-version=\"["); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append(","); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append(")\""); //$NON-NLS-1$
		} else if (minVersion != null) {
			sb.append(";bundle-version=\""); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append("\""); //$NON-NLS-1$
		} else if (maxVersion != null) {
			sb.append(";bundle-version=\""); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append("\""); //$NON-NLS-1$
		}

		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
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
