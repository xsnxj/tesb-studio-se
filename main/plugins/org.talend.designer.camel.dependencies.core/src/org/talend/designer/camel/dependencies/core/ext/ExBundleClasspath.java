package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExBundleClasspath extends AbstractExPredicator<Collection<BundleClasspath>> {

    private static final String SEPARATOR = ";"; //$NON-NLS-1$

    private final String name;
    private final boolean isOptional;

    ExBundleClasspath(String name, boolean isOptional) {
        this.name = name;
        this.isOptional = isOptional;
	}

    @Override
    protected Collection<BundleClasspath> to(NodeType t) {
        for (Object e : t.getElementParameter()) {
            final ElementParameterType p = (ElementParameterType) e;
            if (name.equals(p.getName())) {
                final Collection<BundleClasspath> bundleClasspaths = new HashSet<BundleClasspath>();
                final EList<?> elementValue = p.getElementValue();
                if (elementValue.isEmpty()) {
                    String evtValue = p.getValue();
                    if (evtValue != null) {
                        if (evtValue.startsWith("\"")) { //$NON-NLS-1$
                            evtValue = evtValue.substring(1);
                        }
                        if (evtValue.endsWith("\"")) { //$NON-NLS-1$
                            evtValue = evtValue.substring(0, evtValue.length() - 1);
                        }
                        if (!evtValue.isEmpty()) {
                            for (String name : evtValue.split(SEPARATOR)) {
                                BundleClasspath bundleClasspath = new BundleClasspath();
                                bundleClasspath.setBuiltIn(!isOptional);
                                bundleClasspath.setName(name);
                                bundleClasspaths.add(bundleClasspath);
                            }
                        }
                    }
                } else {
                    for (Object pv : p.getElementValue()) {
                        ElementValueType evt = (ElementValueType) pv;
                        String evtValue = evt.getValue();
                        BundleClasspath bundleClasspath = new BundleClasspath();
                        bundleClasspath.setBuiltIn(!isOptional);
                        bundleClasspath.setName(evtValue);
                        bundleClasspaths.add(bundleClasspath);
                    }
                }
                return bundleClasspaths;
            }
        }
        return null;
    }

}
