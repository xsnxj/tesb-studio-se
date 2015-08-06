package org.talend.designer.camel.dependencies.core.model;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractDependencyItem implements IDependencyItem {

	protected boolean isBuiltIn = false;
	
	protected String name;

	private String description;

	private Collection<String> relativeComponents = new HashSet<String>();

	public void setName(String name) {
		this.name = name;
	}

	@Override
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
			return "Relative Components: " + relativeComponents.toString(); //$NON-NLS-1$
		}
	}

    /**
     * only care about the name, ignore others
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        if (name == null) {
            return false;
        }
        return name.equals(((AbstractDependencyItem) obj).getName());
    }

    /**
     * only care about name
     */
    @Override
    public int hashCode() {
        return name == null ? 0 : name.hashCode();
    }

}
