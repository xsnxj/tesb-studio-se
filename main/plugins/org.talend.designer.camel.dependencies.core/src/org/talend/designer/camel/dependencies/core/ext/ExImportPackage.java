package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collection;
import java.util.HashSet;

import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExImportPackage extends AbstractExPredicator<Collection<ImportPackage>> {

    private static final String SEPARATOR = ","; //$NON-NLS-1$

    private final Collection<ImportPackage> importPackages = new HashSet<ImportPackage>();

    ExImportPackage(String names, boolean isOptional) {
        for (String name : names.split(SEPARATOR)) {
            final ImportPackage importPackage = new ImportPackage();
            importPackage.setBuiltIn(true);
            importPackage.setName(name.trim());
            importPackage.setOptional(isOptional);
            importPackages.add(importPackage);
        }
    }

    protected Collection<ImportPackage> to(NodeType t) {
        return importPackages;
    }

}
