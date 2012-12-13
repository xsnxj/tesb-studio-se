package org.talend.esb.tooling.component.provider;

import java.io.File;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.core.model.components.AbstractComponentsProvider;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;

public class ESBComponentsProvider extends AbstractComponentsProvider {

	private File components;

	@Override
	protected File getExternalComponentsLocation() {
		if (null == components) {
			try {
				components = new File(
					FileLocator.toFileURL(
						FileLocator.find(
							WebServiceComponentPlugin.getDefault().getBundle(),
							new Path("components"),
							null)).toURI());
			} catch (Exception e) {
				WebServiceComponentPlugin.getDefault().getLog().log(
					WebServiceComponentPlugin.getStatus(null, e));
			}
		}
		return components;
	}

}
