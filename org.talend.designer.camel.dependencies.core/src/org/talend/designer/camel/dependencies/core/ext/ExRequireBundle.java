package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExRequireBundle extends AbstractExPredicator<RequireBundle> {

	private String bundleName;
	private String minVersion;
	private String maxVersion;
	private boolean isOptional;

	ExRequireBundle() {
	}

	void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	void setMaxVersion(String maxVersion) {
		this.maxVersion = maxVersion;
	}

	void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	protected Set<RequireBundle> to(NodeType t) {
		RequireBundle requireBundle = toTargetIgnorePredicates();
		Set<RequireBundle> s = new HashSet<RequireBundle>();
		s.add(requireBundle);
		return Collections.unmodifiableSet(s);
	}
	
	@Override
	public RequireBundle toTargetIgnorePredicates() {
		RequireBundle requireBundle = new RequireBundle();
		requireBundle.setBuiltIn(true);
		requireBundle.setMaxVersion(maxVersion);
		requireBundle.setMinVersion(minVersion);
		requireBundle.setName(bundleName);
		requireBundle.setOptional(isOptional);
		return requireBundle;
	}
}
