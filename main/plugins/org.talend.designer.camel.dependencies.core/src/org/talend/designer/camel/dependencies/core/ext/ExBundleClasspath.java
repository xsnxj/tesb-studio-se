package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.camel.dependencies.core.model.BundleClasspath;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExBundleClasspath extends AbstractExPredicator<Set<BundleClasspath>, BundleClasspath> {

	private String attributeName;
	
	private boolean isChecked = true;

	ExBundleClasspath() {
	}
	
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	public boolean isChecked() {
		return isChecked;
	}

	void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	String getAttributeName() {
		return attributeName;
	}

	@Override
	protected Set<BundleClasspath> to(NodeType t) {
		Set<BundleClasspath> bundleClasspaths = new HashSet<BundleClasspath>();
		for (Object e : t.getElementParameter()) {
			ElementParameterType p = (ElementParameterType) e;
			if (attributeName.equals(p.getName())) {
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
							BundleClasspath bundleClasspath = new BundleClasspath();
							bundleClasspath.setChecked(isChecked);
							bundleClasspath.setBuiltIn(true);
							bundleClasspath.setName(evtValue);
							bundleClasspaths.add(bundleClasspath);
						}
					}
				} else {
					for (Object pv : p.getElementValue()) {
						ElementValueType evt = (ElementValueType) pv;
						String evtValue = evt.getValue();
						BundleClasspath bundleClasspath = new BundleClasspath();
						bundleClasspath.setBuiltIn(true);
						bundleClasspath.setChecked(isChecked);
						bundleClasspath.setName(evtValue);
						bundleClasspaths.add(bundleClasspath);
					}
				}
			}
		}

		return Collections.unmodifiableSet(bundleClasspaths);
	}

	@Override
	public BundleClasspath toTargetIgnorePredicates() {
		throw new UnsupportedOperationException();
	}

}
