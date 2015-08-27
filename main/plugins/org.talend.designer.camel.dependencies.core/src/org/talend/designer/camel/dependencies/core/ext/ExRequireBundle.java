package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExRequireBundle extends AbstractExPredicator<RequireBundle> {

	ExRequireBundle() {
	}

	protected RequireBundle to(NodeType t) {
        RequireBundle requireBundle = new RequireBundle();
        requireBundle.setBuiltIn(true);
        requireBundle.setName(name);
        //requireBundle.setVersion(versionRange);
        requireBundle.setOptional(isOptional);
        return requireBundle;
	}

}
