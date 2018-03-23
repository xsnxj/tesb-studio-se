// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.MetadataTable;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.esb.components.ws.tools.extensions.external.IOASDecoder;
import org.talend.designer.esb.components.ws.tools.extensions.external.RestAPIMapping;

/**
 * DOC dsergent class global comment. Detailled comment
 */
public class TRESTRequestNodeAdapter implements TRESTRequestConstants {

    private TRESTRequestNode node;

    private IPreferenceStore prefs;

    public TRESTRequestNodeAdapter(TRESTRequestNode node) {
        this.node = node;
        this.prefs = TRESTRequestPlugin.getDefault().getPreferenceStore();
    }

    @SuppressWarnings("unchecked")
    public IStatus setNodeSetting(IOASDecoder oasManager) {

        // endpoint
        boolean keepEndpointValue = prefs.getBoolean(TRESTRequestConstants.PREF_KEEP_ENDPOINT);
        if (StringUtils.isBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT))) || !keepEndpointValue) {
            node.setParamValue(REST_ENDPOINT, TalendTextUtils.addQuotes(oasManager.getEndpoint()));
        }

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        Map<String, IMetadataTable> savedMetadataTables = getMetadataTablesToKeep(oasManager);

        schemasChildren.clear();

        node.getMetadataList().clear();

        // API Mappings
        for (String key : oasManager.getMappings().keySet()) {

            RestAPIMapping apiStuidioMapping = oasManager.getMappings().get(key);

            Map<String, String> newMapping = new LinkedHashMap<>();

            newMapping.put(HTTP_VERB, apiStuidioMapping.getHttpVerb());
            newMapping.put(URI_PATTERN, TalendTextUtils.addQuotes(apiStuidioMapping.getUriPattern()));
            newMapping.put(CONSUMES, (apiStuidioMapping.getConsumes() != null ? apiStuidioMapping.getConsumes().getLabel() : ""));
            newMapping.put(PRODUCES, (apiStuidioMapping.getProduces() != null ? apiStuidioMapping.getProduces().getLabel() : ""));

            schemasChildren.add(newMapping);

            if (savedMetadataTables.containsKey(apiStuidioMapping.getId())) {

                IMetadataTable table = savedMetadataTables.get(apiStuidioMapping.getId());

                if (StringUtils.isNotBlank(apiStuidioMapping.getOutputFlow())) {
                    newMapping.put(SCHEMA, apiStuidioMapping.getOutputFlow());
                    table.setTableName(apiStuidioMapping.getOutputFlow());
                } else {
                    newMapping.put(SCHEMA, table.getTableName());
                }
                node.getMetadataList().add(table);

            } else {

                newMapping.put(SCHEMA, apiStuidioMapping.getOutputFlow());

                MetadataTable metadataTable = new MetadataTable();
                metadataTable.setTableName(apiStuidioMapping.getOutputFlow());
                node.getMetadataList().add(metadataTable);
            }

        }

        // documentation
        node.setParamValue(COMMENT, oasManager.getDocumentationComment());

        return Status.OK_STATUS;
    }

    private Map<String, IMetadataTable> getMetadataTablesToKeep(IOASDecoder oasManager) {

        Map<String, IMetadataTable> existingMappings = new HashMap<String, IMetadataTable>();

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        for (Map<?, ?> mapping : schemasChildren) {

            String mappingId = getUniqueOperationId(((String) mapping.get(HTTP_VERB)),
                    TalendTextUtils.removeQuotes((String) mapping.get(URI_PATTERN)));

            String tableName = (String) mapping.get(SCHEMA);

            if (oasManager.getMappings().containsKey(mappingId)) {

                for (IMetadataTable table : node.getMetadataList()) {
                    if (tableName.equals(table.getTableName())) {
                        existingMappings.put(mappingId, table);
                    }
                }

            }

        }

        return existingMappings;
    }

    private String getUniqueOperationId(String method, String path) {
        String id = method + "|" + StringUtils.replace(path, "/", "");
        return id.toLowerCase();
    }

    public boolean isEndpointNotNull() {
        return StringUtils.isNotBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT)));
    }

    @SuppressWarnings("unchecked")
    public boolean isNodeToDefaultValues() {

        String endpointDefaultValue = "";
        if (node.getElementParameter(REST_ENDPOINT) != null
                && node.getElementParameter(REST_ENDPOINT).getDefaultValues().get(0) != null) {
            endpointDefaultValue = String
                    .valueOf(node.getElementParameter(REST_ENDPOINT).getDefaultValues().get(0).getDefaultValue());
        }

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        return (endpointDefaultValue.equals(node.getParamStringValue(REST_ENDPOINT))
                || StringUtils.isBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT))))
                && StringUtils.isBlank(node.getParamStringValue(COMMENT)) && schemasChildren.isEmpty();
    }
}
