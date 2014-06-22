package org.talend.esb.tooling.component.provider;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.designer.codegen.additionaljet.AbstractJetFileProvider;
import org.talend.designer.esb.webservice.WebServiceComponentPlugin;

public class ESBJetFileProvider extends AbstractJetFileProvider {

	private static Logger logger = Logger.getLogger(ESBJetFileProvider.class);

	protected File getExternalFrameLocation() {
		try {
			URL localURL1 = FileLocator.find(
					WebServiceComponentPlugin.getDefault().getBundle(),
					new Path("resources"), null);
			URL localURL2 = FileLocator.toFileURL(localURL1);
			return new File(localURL2.getPath());
		} catch (Exception localException) {
			logger.error(localException);
		}
		return null;
	}
}
