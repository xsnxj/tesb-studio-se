package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExRequireBundle extends AbstractExPredicator<RequireBundle> {

    private final RequireBundle requireBundle = new RequireBundle();

    ExRequireBundle(String name, boolean isOptional) {
        requireBundle.setBuiltIn(true);
        requireBundle.setName(name);
        requireBundle.setOptional(isOptional);
    }

    protected RequireBundle to(NodeType t) {
        return requireBundle;
    }

}
