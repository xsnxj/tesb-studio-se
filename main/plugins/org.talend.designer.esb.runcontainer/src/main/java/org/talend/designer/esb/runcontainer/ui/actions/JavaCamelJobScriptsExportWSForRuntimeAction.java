// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.ui.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.core.runtime.IProgressMonitor;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction;
import org.talend.designer.publish.core.models.BundleModel;
import org.talend.designer.publish.core.models.FeaturesModel;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * It's for export the karaf feature to a target folder without zipping.
 *
 * $Id$
 *
 */
public class JavaCamelJobScriptsExportWSForRuntimeAction extends JavaCamelJobScriptsExportWSAction {

    /**
     * DOC yyi JavaCamelJobScriptsExportWSForRuntimeAction constructor comment.
     * 
     * @param routeNode
     * @param version
     * @param bundleVersion
     */
    public JavaCamelJobScriptsExportWSForRuntimeAction(IRepositoryNode routeNode, String version, String bundleVersion) {
        super(routeNode, version, bundleVersion);
        // TODO Auto-generated constructor stub
    }

    /**
     * DOC yyi JavaCamelJobScriptsExportWSForRuntimeAction constructor comment.
     * 
     * @param routeNode
     * @param version
     * @param string
     * @param b
     */
    public JavaCamelJobScriptsExportWSForRuntimeAction(RepositoryNode routeNode, String version, String string, boolean b) {
        // TODO Auto-generated constructor stub
        super(routeNode, version, string, b);
    }

    @Override
    protected void processResults(FeaturesModel featuresModel, IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        String parentFolder = "E:/tmp/alltest/" + featuresModel.getArtifactId() + "/repository";
        try {
            getTempDir();

            File parentDestFile = new File(parentFolder);
            if (!parentDestFile.exists()) {
                parentDestFile.mkdirs();
            }

            // new ZipModel(featuresModel, new File("E:/tmp/alltest/"+featuresModel.getArtifactId()));
            // Create the parent file if not exist
            // BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream("E:/tmp/alltest/"
            // + featuresModel.getArtifactId()));

            try {
                // feature file path:
                // repository/[itemName]/[itemName]-feature/[itemVersion]/[itemName]-feature-[itemVersion].xml
                // FileWriter fw = new FileWriter(new File(PREFIX + featuresModel.getRepositoryLocation(null)));
                // FileOutputStream outputStream = new FileOutputStream(new File(PREFIX +
                // featuresModel.getRepositoryLocation(null)));
                File featureFile = new File(parentFolder + featuresModel.getRepositoryLocation(null));
                featureFile.mkdirs();
                Files.copy(featuresModel.getContent(), featureFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                // add(PREFIX + featuresModel.getRepositoryLocation(null), featuresModel.getContent());
                // output.write(featuresModel.getContent());

                // Bundle File path:
                // repository/[itemName]/[itemVersion]/[itemName]-[itemVersion].jar
                for (BundleModel bundleModel : featuresModel.getBundles()) {
                    // add bundle jar file
                    File f = bundleModel.getFile();
                    if (null == f) {
                        continue;
                    }
                    File bundleFile = new File(parentFolder + bundleModel.getRepositoryLocation(null));
                    bundleFile.mkdirs();
                    Files.copy(bundleModel.getFile().toPath(), bundleFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    // add(PREFIX + bundleModel.getRepositoryLocation(null).toString(), f);
                }
            } finally {
                // output.flush();
                // output.close();
            }
        } catch (IOException e) {
            throw new InvocationTargetException(e);
        }
    }
}
