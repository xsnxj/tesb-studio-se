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
import org.talend.designer.oas.external.IOASDecoder;
import org.talend.designer.oas.external.RestAPIMapping;

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
        if (StringUtils.isBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT)))
                || !keepEndpointValue) {
            node.setParamValue(REST_ENDPOINT, TalendTextUtils.addQuotes(oasManager.getEndpoint()));
        }

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        Map<String, RestAPIMapping> apiDesignerMappings = oasManager.getMappings();

        Map<String, IMetadataTable> savedMetadataTables = getMetadataTablesToKeep(apiDesignerMappings);

        schemasChildren.clear();

        node.getMetadataList().clear();

        // API Mappings
        for (String key : apiDesignerMappings.keySet()) {

            RestAPIMapping apiDesignerMapping = apiDesignerMappings.get(key);

            Map<String, String> newMapping = new LinkedHashMap<>();

            newMapping.put(HTTP_VERB, apiDesignerMapping.getHttpVerb());
            newMapping.put(URI_PATTERN, TalendTextUtils.addQuotes(apiDesignerMapping.getUriPattern()));
            newMapping
                    .put(CONSUMES,
                            (apiDesignerMapping.getConsumes() != null ? apiDesignerMapping.getConsumes().getLabel()
                                    : ""));
            newMapping
                    .put(PRODUCES,
                            (apiDesignerMapping.getProduces() != null ? apiDesignerMapping.getProduces().getLabel()
                                    : ""));

            schemasChildren.add(newMapping);

            if (savedMetadataTables.containsKey(apiDesignerMapping.getId())) {

                IMetadataTable table = savedMetadataTables.get(apiDesignerMapping.getId());

                if (StringUtils.isNotBlank(apiDesignerMapping.getOutputFlow())) {
                    newMapping.put(SCHEMA, apiDesignerMapping.getOutputFlow());
                    table.setTableName(apiDesignerMapping.getOutputFlow());
                } else {
                    newMapping.put(SCHEMA, table.getTableName());
                }
                node.getMetadataList().add(table);

            } else {

                String outputFlow =
                        apiDesignerMapping.getOutputFlow() != null ? apiDesignerMapping.getOutputFlow() : "";

                newMapping.put(SCHEMA, outputFlow);

                MetadataTable metadataTable = new MetadataTable();
                metadataTable.setTableName(outputFlow);
                node.getMetadataList().add(metadataTable);
            }

        }

        // documentation
        node.setParamValue(COMMENT, oasManager.getDocumentationComment());

        return Status.OK_STATUS;
    }

    @SuppressWarnings("unchecked")
    private Map<String, IMetadataTable> getMetadataTablesToKeep(Map<String, RestAPIMapping> apiDesignerMappings) {

        Map<String, IMetadataTable> existingMappings = new HashMap<String, IMetadataTable>();

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        Map<String, String> tableNamesToKeep = new HashMap<>();

        for (Map<?, ?> mapping : schemasChildren) {

            if (mapping.get(HTTP_VERB) instanceof String) {

                String mappingId = getUniqueOperationId(((String) mapping.get(HTTP_VERB)),
                        TalendTextUtils.removeQuotes((String) mapping.get(URI_PATTERN)));

                String tableName = (String) mapping.get(SCHEMA);

                if (!"".equals(tableName) && apiDesignerMappings.containsKey(mappingId)) {
                    tableNamesToKeep.put(tableName, mappingId);
                }
            }
        }

        for (IMetadataTable table : node.getMetadataList()) {
            if (tableNamesToKeep.keySet().contains(table.getTableName())) {
                existingMappings.put(tableNamesToKeep.get(table.getTableName()), table);
            }
        }

        return existingMappings;
    }

    private String getUniqueOperationId(String method, String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String id = method + "|" + path;
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
            endpointDefaultValue =
                    String.valueOf(node.getElementParameter(REST_ENDPOINT).getDefaultValues().get(0).getDefaultValue());
        }

        IElementParameter schemasParameter = node.getElementParameter("SCHEMAS");
        List<Map<?, ?>> schemasChildren = (List<Map<?, ?>>) schemasParameter.getValue();

        return (endpointDefaultValue.equals(node.getParamStringValue(REST_ENDPOINT))
                || StringUtils.isBlank(TalendTextUtils.removeQuotes(node.getParamStringValue(REST_ENDPOINT))))
                && StringUtils.isBlank(node.getParamStringValue(COMMENT)) && schemasChildren.isEmpty();
    }
}
