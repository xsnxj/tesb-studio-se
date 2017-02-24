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
package org.talend.designer.esb.runcontainer.process;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.ITargetExecutionConfig;
import org.talend.core.model.process.JobInfo;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.runprocess.IEclipseProcessor;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.process.ITalendProcessJavaProject;
import org.talend.core.runtime.process.TalendProcessOptionConstants;
import org.talend.designer.core.ISyntaxCheckableEditor;
import org.talend.designer.esb.runcontainer.ui.actions.JavaCamelJobScriptsExportWSForRuntimeAction;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.designer.runprocess.IProcessMessageManager;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 */
public class ESBRuntimeContainerProcessor implements IProcessor, IEclipseProcessor, TalendProcessOptionConstants {

    private IProcess process;

    public ESBRuntimeContainerProcessor(IProcess process) {
        this.process = process;
    }

    @Override
    public void cleanBeforeGenerate(int options) throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#generateCode(boolean, boolean, boolean)
     */
    @Override
    public void generateCode(boolean statistics, boolean trace, boolean context) throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#generateCode(boolean, boolean, boolean, int)
     */
    @Override
    public void generateCode(boolean statistics, boolean trace, boolean context, int option) throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#generateContextCode()
     */
    @Override
    public void generateContextCode() throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#generateEsbFiles()
     */
    @Override
    public void generateEsbFiles() throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#run(int, int, java.lang.String)
     */
    @Override
    public Process run(int statisticsPort, int tracePort, String watchParam) throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#run(int, int, java.lang.String, java.lang.String,
     * org.eclipse.core.runtime.IProgressMonitor, org.talend.designer.runprocess.IProcessMessageManager)
     */
    @Override
    public Process run(int statisticsPort, int tracePort, String watchParam, String log4jLevel, IProgressMonitor monitor,
            IProcessMessageManager processMessageManager) throws ProcessorException {

        ESBRunContainerProcess esbRunContainerProcess = new ESBRunContainerProcess();
        if (process != null) {

            try {
                IRepositoryViewObject routeViewObject = findJob(process.getId());
                RepositoryNode routeNode = new RepositoryNode(routeViewObject, null, ENodeType.REPOSITORY_ELEMENT);
                JavaCamelJobScriptsExportWSForRuntimeAction action = new JavaCamelJobScriptsExportWSForRuntimeAction(routeNode,
                        process.getVersion(), "", true, statisticsPort, tracePort);

                esbRunContainerProcess.startLogging();
                esbRunContainerProcess.getOutputStream().write("Generating bundle to runtime.".toString().getBytes());

                action.run(monitor);
                System.out.println(action.getExportDir());
                String artifactId = routeNode.getObject().getProperty().getDisplayName();
                String artifactVersion = routeNode.getObject().getProperty().getVersion();
                long bundleId = JMXUtil.installBundle(new File(action.getExportDir() + "local_project/" + artifactId + "/"
                        + artifactId + "-bundle/" + artifactVersion + "/" + artifactId + "-bundle-" + artifactVersion + ".jar"));

                esbRunContainerProcess.getOutputStream().write(
                        ("Install bundle, return value: " + bundleId + ".\n").toString().getBytes());
                action.removeTempFilesAfterDeploy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return esbRunContainerProcess;
    }

    private IRepositoryViewObject findJob(String jobID) throws PersistenceException {

        ProxyRepositoryFactory proxyRepositoryFactory = ProxyRepositoryFactory.getInstance();

        return proxyRepositoryFactory.getLastVersion(jobID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#run(java.lang.String[], int, int)
     */
    @Override
    public Process run(String[] optionsParam, int statisticsPort, int tracePort) throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#run(java.lang.String[], int, int,
     * org.eclipse.core.runtime.IProgressMonitor, org.talend.designer.runprocess.IProcessMessageManager)
     */
    @Override
    public Process run(String[] optionsParam, int statisticsPort, int tracePort, IProgressMonitor monitor,
            IProcessMessageManager processMessageManager) throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getCodeContext()
     */
    @Override
    public String getCodeContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getCodePath()
     */
    @Override
    public IPath getCodePath() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getContextPath()
     */
    @Override
    public IPath getContextPath() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getDataSetPath()
     */
    @Override
    public IPath getDataSetPath() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getCodeProject()
     */
    @Override
    public IProject getCodeProject() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getTalendJavaProject()
     */
    @Override
    public ITalendProcessJavaProject getTalendJavaProject() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getLineNumber(java.lang.String)
     */
    @Override
    public int getLineNumber(String nodeName) {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getInterpreter()
     */
    @Override
    public String getInterpreter() throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setInterpreter(java.lang.String)
     */
    @Override
    public void setInterpreter(String interpreter) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setLibraryPath(java.lang.String)
     */
    @Override
    public void setLibraryPath(String libraryPath) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getCodeLocation()
     */
    @Override
    public String getCodeLocation() throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setCodeLocation(java.lang.String)
     */
    @Override
    public void setCodeLocation(String codeLocation) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getProcessorType()
     */
    @Override
    public String getProcessorType() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setProcessorStates(int)
     */
    @Override
    public void setProcessorStates(int states) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.designer.runprocess.IProcessor#setSyntaxCheckableEditor(org.talend.designer.core.ISyntaxCheckableEditor
     * )
     */
    @Override
    public void setSyntaxCheckableEditor(ISyntaxCheckableEditor editor) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getTypeName()
     */
    @Override
    public String getTypeName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#saveLaunchConfiguration()
     */
    @Override
    public Object saveLaunchConfiguration() throws CoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getCommandLine(boolean, boolean, int, int, java.lang.String[])
     */
    @Override
    public String[] getCommandLine(boolean needContext, boolean externalUse, int statOption, int traceOption,
            String... codeOptions) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setContext(org.talend.core.model.process.IContext)
     */
    @Override
    public void setContext(IContext context) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getTargetPlatform()
     */
    @Override
    public String getTargetPlatform() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setTargetPlatform(java.lang.String)
     */
    @Override
    public void setTargetPlatform(String targetPlatform) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#initPath()
     */
    @Override
    public void initPath() throws ProcessorException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getProcess()
     */
    @Override
    public IProcess getProcess() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getContext()
     */
    @Override
    public IContext getContext() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getProperty()
     */
    @Override
    public Property getProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#isCodeGenerated()
     */
    @Override
    public boolean isCodeGenerated() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setCodeGenerated(boolean)
     */
    @Override
    public void setCodeGenerated(boolean codeGenerated) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getProxyParameters()
     */
    @Override
    public String[] getProxyParameters() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setProxyParameters(java.lang.String[])
     */
    @Override
    public void setProxyParameters(String[] proxyParameters) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#syntaxCheck()
     */
    @Override
    public void syntaxCheck() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getMainClass()
     */
    @Override
    public String getMainClass() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getJVMArgs()
     */
    @Override
    public String[] getJVMArgs() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getNeededModules()
     */
    @Override
    public Set<ModuleNeeded> getNeededModules() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getBuildChildrenJobs()
     */
    @Override
    public Set<JobInfo> getBuildChildrenJobs() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setOldBuildJob(boolean)
     */
    @Override
    public void setOldBuildJob(boolean oldBuildJob) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#build(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void build(IProgressMonitor monitor) throws Exception {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#getArguments()
     */
    @Override
    public Map<String, Object> getArguments() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#setArguments(java.util.Map)
     */
    @Override
    public void setArguments(Map<String, Object> argumentsMap) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#cleanWorkingDirectory()
     */
    @Override
    public void cleanWorkingDirectory() throws SecurityException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.runprocess.IEclipseProcessor#debug()
     */
    @Override
    public ILaunchConfiguration debug() throws ProcessorException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.runprocess.IEclipseProcessor#setTargetExecutionConfig(org.talend.core.model.process.
     * ITargetExecutionConfig)
     */
    @Override
    public void setTargetExecutionConfig(ITargetExecutionConfig serverConfiguration) {
        // TODO Auto-generated method stub

    }

}
