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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.talend.designer.esb.runcontainer.ui.RunESBContainerComposite;
import org.talend.designer.esb.runcontainer.ui.actions.JavaCamelJobScriptsExportWSForRuntimeAction;
import org.talend.designer.runprocess.IProcessMessageManager;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class ESBRuntimeContainerProcessor implements IProcessor, IEclipseProcessor, TalendProcessOptionConstants {

    private IProcess process;

    /**
     * DOC yyi ESBRuntimeContainerProcessor constructor comment.
     * 
     * @param process
     */
    public ESBRuntimeContainerProcessor(IProcess process) {
        this.process = process;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.designer.runprocess.IProcessor#cleanBeforeGenerate(int)
     */
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
        // TODO Auto-generated method stub
        ESBRunContainerProcess esbRunContainerProcess = new ESBRunContainerProcess();
        if (process != null) {

            // use the same function with ExportModelJavaProcessor, but will do for maven
            // ProcessItem processItem = (ProcessItem) process;

            try {

                // IProcess2 process = (IProcess2) getProcess();
                IRepositoryViewObject routeViewObject = findJob(process.getId());

                RepositoryNode routeNode = new RepositoryNode(routeViewObject, null, ENodeType.REPOSITORY_ELEMENT);
                JavaCamelJobScriptsExportWSForRuntimeAction action = new JavaCamelJobScriptsExportWSForRuntimeAction(routeNode,
                        process.getVersion(), "", true);
                action.run(new NullProgressMonitor());
                System.out.println("------>" + RunESBContainerComposite.getProcessContext().isRunning());

                String artifactId = routeNode.getObject().getProperty().getDisplayName();

                // processContext.addMessage(new ProcessMessage(MsgType.CORE_ERR, "Starting log listener"));
                String username = "karaf";
                String password = "karaf";

                String host = "localhost";
                String jmxPort = "44444";
                String karafPort = "1099";
                String instanceName = "trun";
                String serviceUrl = "service:jmx:rmi://" + host + ":" + jmxPort + "/jndi/rmi://" + host + ":" + karafPort
                        + "/karaf-" + instanceName;

                HashMap<String, String[]> env = new HashMap<String, String[]>();
                String[] credentials = new String[] { username, password };
                env.put("jmx.remote.credentials", credentials);

                JMXServiceURL url = new JMXServiceURL(serviceUrl);
                JMXConnector jmxc = JMXConnectorFactory.connect(url, env);
                MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

                // String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=feature,name=trun";
                String KARAF_BUNDLE_MBEAN = "org.apache.karaf:type=bundle,name=trun";
                ObjectName objectName = new ObjectName(KARAF_BUNDLE_MBEAN);

                // mbsc.invoke(objectName, "addRepository", new Object[] { "file:E:/tmp/alltest/" + artifactId
                // + "-feature/repository/local_project/" + artifactId + "/" + artifactId + "-bundle/0.1/" + artifactId
                // + "-bundle-0.1.jar" }, new String[] { String.class.getName() });

                Object bundleId = mbsc.invoke(objectName, "install", new Object[] { "file:E:/tmp/alltest/" + artifactId
                        + "-feature/repository/local_project/" + artifactId + "/" + artifactId + "-bundle/0.1/" + artifactId
                        + "-bundle-0.1.jar" }, new String[] { String.class.getName() });

                // Object info = mbsc.invoke(objectName, "infoFeature", new Object[] { artifactId + "-feature" },
                // new String[] { String.class.getName() });
                if (bundleId instanceof Long) {
                    esbRunContainerProcess.getOutputStream().write(bundleId.toString().getBytes());
                    mbsc.invoke(objectName, "start", new Object[] { bundleId.toString() }, new String[] { String.class.getName() });
                }
                jmxc.close();
                // if (info instanceof Long) {
                // System.out.println("------>" + (Long) info);
                // processContext
                // .addMessage(new ProcessMessage(MsgType.CORE_OUT, ">>>>>>>>deployed success!" +
                // info.toString()));
                // } else if (info instanceof TabularDataSupport) {
                //
                // processContext.addMessage(new ProcessMessage(MsgType.STD_OUT, ((TabularDataSupport)
                // info).get("Bundles")
                // .toString()));
                // }
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
