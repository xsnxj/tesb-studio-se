// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.build;

import java.util.List;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IFile;
import org.talend.designer.runprocess.IProcessor;

public class CreateRouteletMavenBundlePom extends CreateMavenBundlePom {

    /**
     * DOC sunchaoqun CreateMavenCamelPom constructor comment.
     *
     * @param jobProcessor
     * @param pomFile
     */
    public CreateRouteletMavenBundlePom(IProcessor jobProcessor, IFile pomFile) {
        super(jobProcessor, pomFile);
    }

    @Override
    protected void updateBundleMainfest(Model bundleModel) {
        for (Profile profile : bundleModel.getProfiles()) {
            if ("packaging-and-assembly".equals(profile.getId())) {
                List<Plugin> plugins = profile.getBuild().getPlugins();
                for (Plugin plugin : plugins) {
                    if ("maven-assembly-plugin".equals(plugin.getArtifactId())) {
                        PluginExecution pluginExecution = plugin.getExecutionsAsMap().get("default");
                        Xpp3Dom configuration = (Xpp3Dom) pluginExecution.getConfiguration();
                        Xpp3Dom archive = new Xpp3Dom("archive");
                        Xpp3Dom manifestFile = new Xpp3Dom("manifestFile");
                        manifestFile.setValue("${current.bundle.resources.dir}/META-INF/MANIFEST.MF");
                        archive.addChild(manifestFile);
                        bundleModel.setBuild(new Build());
                        bundleModel.getBuild().addPlugin(plugin);
                        break;
                    }
                }
                break;
            }
        }
    }
}
