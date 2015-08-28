package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

abstract class AbstractExPredicator<T> {

    private static final String SEPARATOR = ":"; //$NON-NLS-1$

    private final Map<String, String> predicates = new HashMap<String, String>();

    void addPredicate(String name, String value) {
        predicates.put(name, value);
    }

    private boolean satisfy(NodeType n) {
        if (predicates.isEmpty()) {
            return true;
        }
        final Collection<?> parameters = n.getElementParameter();
        for (Map.Entry<String, String> predicate : predicates.entrySet()) {
            String attributeName = predicate.getKey();
            final String attributeValue = predicate.getValue();

            final String[] segments = attributeName.split(SEPARATOR);
            String valueName = null;
            if (segments.length > 1) {
                attributeName = segments[0];
                valueName = segments[1];
            }
            for (Object o : parameters) {
                final ElementParameterType ept = (ElementParameterType) o;
                if (ept.getName().equals(attributeName)) {
                    if (null == valueName) {
                        if (!attributeValue.equals(ept.getValue())) {
                            return false;
                        }
                    } else {
                        boolean found = false;
                        for (Object e : ept.getElementValue()) {
                            final ElementValueType evt = (ElementValueType) e;
                            if (valueName.equals(evt.getElementRef())) {
                                if (attributeValue.equals(evt.getValue())) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            return false;
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }

    public T toTargets(NodeType t) {
        if (satisfy(t)) {
            return to(t);
        }
        return null;
    }

    protected abstract T to(NodeType t);

}
