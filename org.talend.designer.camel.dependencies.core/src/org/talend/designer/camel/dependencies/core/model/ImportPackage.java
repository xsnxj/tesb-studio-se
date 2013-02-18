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
		if (split.length <= 1) {
			return;
		}
		for (int i = 1; i < split.length; i++) {
			String s = split[i];
			if ("resolution:=optional".equals(s)) { //$NON-NLS-1$
				setOptional(true);
			} else if (s.startsWith("version=")) { //$NON-NLS-1$
				parseVersions(s);
			}
		}
	}

	private void parseVersions(String input) {
		int firstQuote = input.indexOf("\""); //$NON-NLS-1$
		int lastQuote = input.lastIndexOf("\""); //$NON-NLS-1$
		setVersionRange(input.substring(firstQuote + 1, lastQuote));
	}

	@Override
	public int getType() {
		return IMPORT_PACKAGE;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(";version=\"");
		sb.append(getVersionRange());
		sb.append("\"");
		
		if (isOptional) {
			sb.append(";resolution:=optional"); //$NON-NLS-1$
		}

		return sb.toString();
	}

	@Override
	protected String getVersionPrefix() {
		return "version";
	}
}
