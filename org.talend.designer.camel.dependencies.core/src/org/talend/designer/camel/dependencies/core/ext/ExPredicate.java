package org.talend.designer.camel.dependencies.core.ext;

public class ExPredicate {

	private String attributeName;
	private String attributeValue;
	private boolean isRegex = false;

	ExPredicate() {
	}

	void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	void setRegex(boolean isRegex) {
		this.isRegex = isRegex;
	}

	String getAttributeName() {
		return attributeName;
	}

	String getAttributeValue() {
		return attributeValue;
	}

	boolean isRegex() {
		return isRegex;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ExPredicate)) {
			return false;
		}
		if (attributeName == null)
			return false;
		return attributeName.equals(((ExPredicate) obj).attributeName);
	}

	@Override
	public int hashCode() {
		return attributeName == null ? super.hashCode() : attributeName
				.hashCode();
	}
}
