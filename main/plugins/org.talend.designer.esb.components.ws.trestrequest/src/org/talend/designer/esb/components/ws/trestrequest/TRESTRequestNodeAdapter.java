// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.esb.components.ws.trestrequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.esb.components.ws.tools.external.OASManager;
import org.talend.designer.esb.components.ws.tools.external.RestAPIMapping;

/**
 * DOC dsergent class global comment. Detailled comment
 */
public class TRESTRequestNodeAdapter implements TRESTRequestConstants {

    protected TRESTRequestNode node;

    public TRESTRequestNodeAdapter(TRESTRequestNode node) {
        this.node = node;
    }

    @SuppressWarnings("unchecked")
    public IStatus setNodeSetting(OASManager oasManager) {

        // endpoint
        node.setParamValue(REST_ENDPOINT, TalendTextUtils.addQuotes(oasManager.getEndpoint()));

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();
        schemasChildren.clear();

        // API Mappings
        for (RestAPIMapping mapping : oasManager.getMappings()) {

            Map<String, String> newMapping = new LinkedHashMap<>();

            newMapping.put(SCHEMA, mapping.getOutputFlow());
            newMapping.put(HTTP_VERB, mapping.getHttpVerb());
            newMapping.put(URI_PATTERN, TalendTextUtils.addQuotes(mapping.getUriPattern()));
            newMapping.put(CONSUMES, (mapping.getConsumes() != null ? mapping.getConsumes().getLabel() : ""));
            newMapping.put(PRODUCES, (mapping.getProduces() != null ? mapping.getProduces().getLabel() : ""));

            schemasChildren.add(newMapping);

        }

        // documentation
        node.setParamValue(COMMENT, oasManager.getDocumentationComment());

        return Status.OK_STATUS;
    }

    @SuppressWarnings("unchecked")
    public boolean isNodeToDefaultValues() {
        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        return StringUtils.isBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT)))
                && StringUtils.isBlank(node.getParamStringValue(COMMENT)) && schemasChildren.isEmpty();
    }
}
