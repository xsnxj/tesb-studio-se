package org.talend.camel.designer.codegen.jet;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.camel.designer.codegen.Activator;
import org.talend.designer.codegen.additionaljet.AbstractJetFileProvider;

public class CamelJetFileProvider extends AbstractJetFileProvider {

	 private File providedLocation = null;

	 @Override
	 protected File getExternalFrameLocation() {
	     if (null == providedLocation) {
	         Activator plugin = Activator.getDefault();
	         try {
	             URL url = FileLocator.find(plugin.getBundle(), new Path("resources"), null); //$NON-NLS-1$
	             url = FileLocator.toFileURL(url);
	             providedLocation = new File(url.getPath());
	         } catch (Exception e) {
	             //ignore.
	         }
	     }
	     return providedLocation;
	 }

}
