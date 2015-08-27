package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.ImportPackage;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExImportPackage extends AbstractExPredicator<ImportPackage> {

	ExImportPackage() {
	}

	protected ImportPackage to(NodeType t) {
        ImportPackage importPackage = new ImportPackage();
        importPackage.setBuiltIn(true);
        //importPackage.setVersion(versionRange);
        importPackage.setName(name);
        importPackage.setOptional(isOptional);
        return importPackage;
	}

}
