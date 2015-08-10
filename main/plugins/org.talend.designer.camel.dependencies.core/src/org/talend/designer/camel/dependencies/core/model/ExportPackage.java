package org.talend.designer.camel.dependencies.core.model;


public class ExportPackage extends OsgiDependencies {

    public ExportPackage() {
    }

    public ExportPackage(String inputString) {
        super(inputString);
    }

    @Override
    public int getType() {
        return EXPORT_PACKAGE;
    }

    @Override
    protected String getVersionPrefix() {
        return "version";
    }

}
