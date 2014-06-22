package org.talend.designer.camel.dependencies.core.model;


public class ImportPackage extends OsgiDependencies<ImportPackage> {

	public ImportPackage() {
		super();
	}

	public ImportPackage(String inputString) {
		super(inputString);
	}

	public ImportPackage(ImportPackage copied) {
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
			}else if(s.startsWith("version=")){ //$NON-NLS-1$
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
	public int getType() {
		return IMPORT_PACKAGE;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);

		if (minVersion != null && maxVersion != null) {
			sb.append(";version=\"["); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append(","); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append(")\""); //$NON-NLS-1$
		} else if (minVersion != null) {
			sb.append(";version=\""); //$NON-NLS-1$
			sb.append(minVersion);
			sb.append("\""); //$NON-NLS-1$
		} else if (maxVersion != null) {
			sb.append(";version=\""); //$NON-NLS-1$
			sb.append(maxVersion);
			sb.append("\""); //$NON-NLS-1$
		}

		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	// public static void main(String[] args) {
	// ImportPackage importPackage = new ImportPackage();
	// importPackage.setName("aaa");
	// System.out.println(importPackage);
	//
	// importPackage.setMinVersion("1.0.0");
	// System.out.println(importPackage);
	//
	// importPackage.setMaxVersion("2.0.0");
	// System.out.println(importPackage);
	//
	// importPackage.setMinVersion(null);
	// System.out.println(importPackage);
	//
	// importPackage.setMaxVersion(null);
	// System.out.println(importPackage);
	//
	// importPackage.setMinVersion("1.0.0");
	// System.out.println(importPackage);
	//
	// importPackage.setOptional(true);
	// System.out.println(importPackage);
	//
	// importPackage.setMaxVersion("2.0.0");
	// System.out.println(importPackage);
	// }
}
