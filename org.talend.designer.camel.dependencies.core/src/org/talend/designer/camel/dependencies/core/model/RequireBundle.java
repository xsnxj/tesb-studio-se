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
		String[] split = inputString.split(";");
		setName(split[0]);
		if(split.length<=1){
			return;
		}
		for(int i = 1;i<split.length;i++){
			String s = split[i];
			if("resolution:=optional".equals(s)){
				setOptional(true);
			}else if(s.startsWith("bundle-version=")){
				parseVersions(s);
			}
		}
	}
	
	private void parseVersions(String input) {
		int firstQuote = input.indexOf("\"");
		int lastQuote = input.lastIndexOf("\"");
		int commaIndex = input.indexOf(",");
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
