// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.codegen.jet;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.talend.camel.designer.codegen.Activator;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.designer.codegen.additionaljet.AbstractJetFileProvider;

public class CamelJetFileProvider extends AbstractJetFileProvider {

    private File providedLocation;

    @Override
    protected File getExternalFrameLocation() {
        if (null == providedLocation) {
            URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path("resources"), null); //$NON-NLS-1$
            try {
                url = FileLocator.toFileURL(url);
                providedLocation = new File(url.getPath());
            } catch (IOException e) {
                ExceptionHandler.process(e);
            }
        }
        return providedLocation;
    }

}
