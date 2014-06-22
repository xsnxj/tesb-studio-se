package org.talend.designer.camel.dependencies.core.ext;

import org.talend.designer.camel.dependencies.core.model.RequireBundle;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public class ExRequireBundle extends AbstractExPredicator<RequireBundle, RequireBundle> {

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

	protected RequireBundle to(NodeType t) {
		RequireBundle requireBundle = toTargetIgnorePredicates();
		return requireBundle;
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
