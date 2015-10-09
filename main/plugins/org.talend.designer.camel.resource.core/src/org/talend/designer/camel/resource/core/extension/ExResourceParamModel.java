// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.core.extension;

import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;

/**
 * @author xpli
 * 
 */
public class ExResourceParamModel {

    private final String paramName;

    public ExResourceParamModel(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public boolean eualate(INode node) {
        final IElementParameter parameter = node.getElementParameter(paramName);
        if (parameter != null) {
            return parameter.isShow(node.getElementParameters());
        }
        return false;
    }

}
