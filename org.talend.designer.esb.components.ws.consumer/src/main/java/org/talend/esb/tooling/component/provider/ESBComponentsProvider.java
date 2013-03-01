package org.talend.esb.tooling.component.provider;

import java.io.File;
import java.net.URL;

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
				URL url = FileLocator.find(
						WebServiceComponentPlugin.getDefault().getBundle(),
						new Path("components"),	null);
				URL fileUrl = FileLocator.toFileURL(url);
				components = new File(fileUrl.getPath());
			} catch (Exception e) {
				WebServiceComponentPlugin.getDefault().getLog().log(
						WebServiceComponentPlugin.getStatus(null, e));
			}
		}
		return components;
	}

}
