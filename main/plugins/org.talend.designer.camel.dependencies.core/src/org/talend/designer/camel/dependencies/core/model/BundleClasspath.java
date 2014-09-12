package org.talend.designer.camel.dependencies.core.model;


public class BundleClasspath extends AbstractDependencyItem {

	public BundleClasspath(){
	}
	
	public BundleClasspath(String input) {
		parse(input);
	}

	protected void parse(String inputString) {
		String[] split = inputString.split(";"); //$NON-NLS-1$
		setName(split.length < 1 ? null : split[0]);
		if (split.length <= 1) {
			return;
		}
		try {
			setChecked(Boolean.parseBoolean(split[1]));
		} catch (Exception e) {
		}
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
		if (name == null) {
			return false;
		}
		return name.equals(((BundleClasspath) obj).getName());
	}

	@Override
	public int hashCode() {
		return name == null ? super.hashCode() : name.hashCode();
	}

	public int isValid() {
		if(name == null || name.equals("")){ //$NON-NLS-1$
			return NAME_NULL;
		}
		return OK;
	}

	@Override
	public String getLabel() {
		return name;
	}

	@Override
	public String toManifestString() {
		return name;
	}

	@Override
	public int getType() {
		return CLASS_PATH;
	}

	@Override
	public boolean strictEqual(Object obj) {
		return equals(obj);
	}
}
