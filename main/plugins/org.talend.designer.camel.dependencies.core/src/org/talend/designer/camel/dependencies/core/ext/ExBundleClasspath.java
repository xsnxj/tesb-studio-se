package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExBundleClasspath extends AbstractExPredicator<Collection<BundleClasspath>> {

	ExBundleClasspath() {
	}

	@Override
	protected Collection<BundleClasspath> to(NodeType t) {
	    Collection<BundleClasspath> bundleClasspaths = new HashSet<BundleClasspath>();
		for (Object e : t.getElementParameter()) {
			ElementParameterType p = (ElementParameterType) e;
			if (name.equals(p.getName())) {
				EList<?> elementValue = p.getElementValue();
				if (elementValue.isEmpty()) {
					String evtValue = p.getValue();
					if (evtValue != null) {
						if (evtValue.startsWith("\"")) { //$NON-NLS-1$
							evtValue = evtValue.substring(1);
						}
						if (evtValue.endsWith("\"")) { //$NON-NLS-1$
							evtValue = evtValue.substring(0,
									evtValue.length() - 1);
						}
						if (!evtValue.trim().equals("")) { //$NON-NLS-1$
							String[] names = StringUtils.split(evtValue, ';');
							for (String name : names) {
								BundleClasspath bundleClasspath = new BundleClasspath();
								bundleClasspath.setOptional(isOptional);
								bundleClasspath.setBuiltIn(true);
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
						bundleClasspath.setBuiltIn(true);
						bundleClasspath.setOptional(isOptional);
						bundleClasspath.setName(evtValue);
						bundleClasspaths.add(bundleClasspath);
					}
				}
			}
		}

		return bundleClasspaths;
	}

}
