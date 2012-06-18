package org.talend.designer.camel.dependencies.core.model;

public class BundleClasspath implements IDependencyItem {

	private String path = null;

	private boolean isBuiltIn = false;

	public void setBuiltIn(boolean isBuiltIn) {
		this.isBuiltIn = isBuiltIn;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	@Override
	public boolean isBuiltIn() {
		return isBuiltIn;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof BundleClasspath)) {
			return false;
		}
		if (path == null) {
			return false;
		}
		return path.equals(((BundleClasspath) obj).getPath());
	}

	@Override
	public int hashCode() {
		return path == null ? super.hashCode() : path.hashCode();
	}

	public boolean isValid() {
		return path != null;
	}

	@Override
	public String getLabel() {
		return path;
	}

	@Override
	public String toString() {
		return path;
	}

	@Override
	public int getType() {
		return CLASS_PATH;
	}
}
