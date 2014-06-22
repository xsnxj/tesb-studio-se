package org.talend.designer.camel.dependencies.core.model;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractDependencyItem implements IDependencyItem{

	protected boolean isBuiltIn = false;
	
	protected String name = null;
	
	// version regular expression
	protected static final String VERSION_REGEX = "\\d+\\.\\d+\\.\\d+(\\.(\\w|-|_)+)?"; //$NON-NLS-1$
	
	// name regular expression
	protected static final String NAME_PATTERN = "[^\\s;=\"\\[\\]\\(\\),:|]+"; //$NON-NLS-1$

	protected Pattern versionPattern = Pattern.compile(VERSION_REGEX);
	protected Pattern namePattern = Pattern.compile(NAME_PATTERN);
	
	private String description;
	
	private Set<String> relativeComponents = new HashSet<String>(5);

	protected boolean isChecked;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setBuiltIn(boolean isBuiltIn) {
		this.isBuiltIn = isBuiltIn;
	}
	
	@Override
	public boolean isBuiltIn() {
		return isBuiltIn;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void addRelativeComponent(String componentUniqueName){
		relativeComponents.add(componentUniqueName);
	}
	
	@Override
	public String getDescription() {
		if (relativeComponents.isEmpty() && description == null) {
			return ""; //$NON-NLS-1$
		} else if (relativeComponents.isEmpty()) {
			return description;
		} else {
			return "Relative Components: "+relativeComponents.toString(); //$NON-NLS-1$
		}
	}
	
	protected String normalizeVersion(String version) {
		if (version == null || version.trim().equals("")) //$NON-NLS-1$
			return null;
		int dotCount = 0;
		int length = version.length();
		for (int i = 0; i < length; i++) {
			if ('.' == version.charAt(i)) {
				dotCount++;
			}
		}
		dotCount = 2 - dotCount;
		if (dotCount == 0) {
			return version;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(version);
		for (int i = 0; i < dotCount; i++) {
			sb.append(".0"); //$NON-NLS-1$
		}
		return sb.toString();
	}
	
	@Override
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public boolean isChecked() {
		return this.isChecked;
	}
	
	public static final int OK = 0;
	public static final int NAME_NULL = 1;
	public static final int NAME_INVALID = 2;
	public abstract int isValid();
}
