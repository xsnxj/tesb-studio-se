package org.talend.designer.camel.dependencies.core.model;

public class BundleClasspath extends AbstractDependencyItem {

    private boolean isChecked;

    public BundleClasspath() {
    }

    public BundleClasspath(String input) {
        parse(input);
    }

    protected void parse(String inputString) {
        String[] split = inputString.split(";"); //$NON-NLS-1$
        setName(split[0]);
        if (split.length > 1) {
            setChecked(Boolean.parseBoolean(split[1]));
        }
    }

    @Override
    public String getLabel() {
        return name;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public String toString() {
        return isChecked ? name : null;
    }

    @Override
    public int getType() {
        return CLASS_PATH;
    }

}
