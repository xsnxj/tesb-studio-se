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
package org.talend.designer.esb.runcontainer.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.talend.camel.designer.ui.wizards.actions.JavaCamelJobScriptsExportWSAction;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.components.ComponentCategory;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IProcess;
import org.talend.core.model.process.ITargetExecutionConfig;
import org.talend.core.model.process.JobInfo;
import org.talend.core.model.process.ProcessUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.runprocess.IEclipseProcessor;
import org.talend.core.repository.constants.FileConstants;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.process.ITalendProcessJavaProject;
import org.talend.core.runtime.process.TalendProcessOptionConstants;
import org.talend.designer.core.ISyntaxCheckableEditor;
import org.talend.designer.esb.runcontainer.export.JobJavaScriptOSGIForESBRuntimeManager;
import org.talend.designer.esb.runcontainer.ui.actions.StartRuntimeAction;
import org.talend.designer.esb.runcontainer.util.JMXUtil;
import org.talend.designer.runprocess.IProcessMessageManager;
import org.talend.designer.runprocess.IProcessor;
import org.talend.designer.runprocess.ProcessMessage;
import org.talend.designer.runprocess.ProcessMessage.MsgType;
import org.talend.designer.runprocess.ProcessorException;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.ui.action.ExportServiceAction;
import org.talend.repository.ui.wizards.exportjob.action.JobExportAction;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManager;
import org.talend.repository.ui.wizards.exportjob.scriptsmanager.JobScriptsManagerFactory;

public class RunContainerProcessor implements IProcessor, IEclipseProcessor, TalendProcessOptionConstants {

    private IProcess process;

    private JmxDeployJob esbContainerJob;

    public RunContainerProcessor(IProcess process) {
        this.process = process;
    }

    @Override
    public void cleanBeforeGenerate(int options) throws ProcessorException {

    }

    @Override
    public void generateCode(boolean statistics, boolean trace, boolean context) throws ProcessorException {

    }

    @Override
    public void generateCode(boolean statistics, boolean trace, boolean context, int option) throws ProcessorException {

    }

    @Override
    public void generateContextCode() throws ProcessorException {

    }

    @Override
    public void generateEsbFiles() throws ProcessorException {

    }

    @Override
    public Process run(int statisticsPort, int tracePort, String watchParam) throws ProcessorException {

        return null;
    }

    @Override
    public Process run(int statisticsPort, int tracePort, String watchParam, String log4jLevel, IProgressMonitor monitor,
            IProcessMessageManager processMessageManager) throws ProcessorException {

        new StartRuntimeAction().run();

        RunContainerProcess esbRunContainerProcess = new RunContainerProcess();
        esbRunContainerProcess.startLogging();
        esbContainerJob = null;
        esbContainerJob = new JmxDeployJob(processMessageManager, statisticsPort, tracePort);
        esbContainerJob.install();

        do {
            // The process need to wait jxm job starts to return
        } while (esbContainerJob.getResult() == null);

        if (esbContainerJob.getResult() != Status.OK_STATUS) {
            esbRunContainerProcess.stopLogging();
            throw new ProcessorException(esbContainerJob.getResult().getException());
        }
        return esbRunContainerProcess;
    }

    private IRepositoryViewObject findJob(String jobID) throws PersistenceException {

        ProxyRepositoryFactory proxyRepositoryFactory = ProxyRepositoryFactory.getInstance();
        return proxyRepositoryFactory.getLastVersion(jobID);
    }

    /**
     * To write log to console before the process starts, for some jmx perparing operations
     * 
     * @param processMessageManager
     * @param message
     */
    private void writeLog(IProcessMessageManager processMessageManager, ProcessMessage message) {
        processMessageManager.addMessage(message);
        // processMessageManager.updateConsole();
    }

    @Override
    public Process run(String[] optionsParam, int statisticsPort, int tracePort) throws ProcessorException {

        return null;
    }

    @Override
    public Process run(String[] optionsParam, int statisticsPort, int tracePort, IProgressMonitor monitor,
            IProcessMessageManager processMessageManager) throws ProcessorException {

        return null;
    }

    @Override
    public String getCodeContext() {

        return null;
    }

    @Override
    public IPath getCodePath() {

        return null;
    }

    @Override
    public IPath getContextPath() {

        return null;
    }

    @Override
    public IPath getDataSetPath() {

        return null;
    }

    @Override
    public IProject getCodeProject() {

        return null;
    }

    @Override
    public ITalendProcessJavaProject getTalendJavaProject() {

        return null;
    }

    @Override
    public int getLineNumber(String nodeName) {

        return 0;
    }

    @Override
    public String getInterpreter() throws ProcessorException {

        return null;
    }

    @Override
    public void setInterpreter(String interpreter) {

    }

    @Override
    public void setLibraryPath(String libraryPath) {

    }

    @Override
    public String getCodeLocation() throws ProcessorException {

        return null;
    }

    @Override
    public void setCodeLocation(String codeLocation) {

    }

    @Override
    public String getProcessorType() {

        return null;
    }

    @Override
    public void setProcessorStates(int states) {

    }

    @Override
    public void setSyntaxCheckableEditor(ISyntaxCheckableEditor editor) {

    }

    @Override
    public String getTypeName() {

        return null;
    }

    @Override
    public Object saveLaunchConfiguration() throws CoreException {

        return null;
    }

    @Override
    public String[] getCommandLine(boolean needContext, boolean externalUse, int statOption, int traceOption,
            String... codeOptions) {

        return null;
    }

    @Override
    public void setContext(IContext context) {

    }

    @Override
    public String getTargetPlatform() {

        return null;
    }

    @Override
    public void setTargetPlatform(String targetPlatform) {

    }

    @Override
    public void initPath() throws ProcessorException {

    }

    @Override
    public IProcess getProcess() {

        return null;
    }

    @Override
    public IContext getContext() {

        return null;
    }

    @Override
    public Property getProperty() {

        return null;
    }

    @Override
    public boolean isCodeGenerated() {

        return false;
    }

    @Override
    public void setCodeGenerated(boolean codeGenerated) {

    }

    @Override
    public String[] getProxyParameters() {

        return null;
    }

    @Override
    public void setProxyParameters(String[] proxyParameters) {

    }

    @Override
    public void syntaxCheck() {

    }

    @Override
    public String getMainClass() {

        return null;
    }

    @Override
    public String[] getJVMArgs() {

        return null;
    }

    @Override
    public Set<ModuleNeeded> getNeededModules() {

        return null;
    }

    @Override
    public Set<JobInfo> getBuildChildrenJobs() {

        return null;
    }

    @Override
    public void setOldBuildJob(boolean oldBuildJob) {

    }

    @Override
    public void build(IProgressMonitor monitor) throws Exception {

    }

    @Override
    public Map<String, Object> getArguments() {

        return null;
    }

    @Override
    public void setArguments(Map<String, Object> argumentsMap) {

    }

    @Override
    public void cleanWorkingDirectory() throws SecurityException {

    }

    @Override
    public ILaunchConfiguration debug() throws ProcessorException {

        return null;
    }

    @Override
    public void setTargetExecutionConfig(ITargetExecutionConfig serverConfiguration) {

    }

    private final class JmxDeployJob extends Job {

        private IProcessMessageManager processMessageManager;

        private int statisticsPort;

        private int tracePort;

        protected String[] kars;

        protected long[] bundles;

        private boolean isInstall;

        /**
         * DOC yyan JmxDeployJob constructor comment.
         * 
         * @param processMessageManager
         * @param statisticsPort
         * @param tracePort
         */
        private JmxDeployJob(IProcessMessageManager processMessageManager, int statisticsPort, int tracePort) {
            super("ESB Container with JMX");
            this.processMessageManager = processMessageManager;
            this.statisticsPort = statisticsPort;
            this.tracePort = tracePort;
        }

        public void install() {
            isInstall = true;
            schedule();
        }

        public void unInstall() {
            isInstall = false;
            schedule();
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            IStatus status = null;
            if (process != null) {
                if (isInstall) {
                    status = doInstall(monitor);
                } else {
                    status = doUninstall(monitor);
                }
            }
            return status;
        }

        protected IStatus doUninstall(IProgressMonitor monitor) {

            try {
                if (bundles != null && bundles.length > 0) {
                    for (long bundle : bundles) {
                        JMXUtil.uninstallBundle(bundle);
                    }
                }
                if (kars != null && kars.length > 0) {
                    for (String kar : kars) {
                        JMXUtil.uninstallKar(kar);
                    }
                }
            } catch (Exception e) {
                // writeLog(processMessageManager, new ProcessMessage(MsgType.STD_ERR, ExceptionUtils.getStackTrace(e) +
                // ".\n"));
                return new Status(IStatus.WARNING, "org.talend.designer.esb.runcontainer", "Kill process might failed.", e);
            }
            return Status.OK_STATUS;
        }

        protected IStatus doInstall(IProgressMonitor monitor) {

            File target = null;
            try {
                IRepositoryViewObject viewObject = findJob(process.getId());
                RepositoryNode node = new RepositoryNode(viewObject, null, ENodeType.REPOSITORY_ELEMENT);

                if (ComponentCategory.CATEGORY_4_DI.getName().equals(process.getComponentsType())) {
                    // publish job
                    ProcessItem processItem = (ProcessItem) node.getObject().getProperty().getItem();
                    List<Item> items = new ArrayList<Item>(1);
                    items.add(processItem);
                    Collection<IRepositoryViewObject> allDependencies = ProcessUtils.getProcessDependencies(
                            ERepositoryObjectType.METADATA, items, false);
                    // check service
                    if (!allDependencies.isEmpty()) {
                        target = File.createTempFile("service", FileConstants.KAR_FILE_SUFFIX, null);
                        for (IRepositoryViewObject object : allDependencies) {
                            if (object.getProperty().getItem() != null && object.getProperty().getItem() instanceof ServiceItem) {
                                ServiceItem serviceItem = (ServiceItem) object.getProperty().getItem();
                                IRunnableWithProgress action = new ExportServiceAction(serviceItem, target.getAbsolutePath(),
                                        null);
                                action.run(monitor);
                                kars = JMXUtil.installKar(target);
                                writeLog(processMessageManager, new ProcessMessage(MsgType.STD_OUT, "Install kar, return value: "
                                        + Arrays.toString(kars) + ".\n"));
                            }
                        }
                    } else {
                        target = File.createTempFile("job", FileConstants.JAR_FILE_SUFFIX, null);
                        JobScriptsManager jobScriptsManager = new JobJavaScriptOSGIForESBRuntimeManager(
                                JobScriptsManagerFactory.getDefaultExportChoiceMap(), processItem.getProcess()
                                        .getDefaultContext(), JobScriptsManager.LAUNCHER_ALL, statisticsPort, tracePort);
                        // generate
                        jobScriptsManager.setDestinationPath(target.getAbsolutePath());
                        JobExportAction jobAction = new JobExportAction(Collections.singletonList(node), node.getObject()
                                .getProperty().getVersion(), node.getObject().getProperty().getVersion(), jobScriptsManager,
                                System.getProperty("java.io.tmpdir"));

                        jobAction.run(monitor);
                        bundles = JMXUtil.installBundle(target);
                        writeLog(processMessageManager, new ProcessMessage(MsgType.STD_OUT, "Install bundle, return value: "
                                + Arrays.toString(bundles) + ".\n"));
                    }
                } else if (ComponentCategory.CATEGORY_4_CAMEL.getName().equals(process.getComponentsType())) {
                    // publish route
                    target = File.createTempFile("route", FileConstants.KAR_FILE_SUFFIX, null);
                    JavaCamelJobScriptsExportWSAction camelAction = new JavaCamelJobScriptsExportWSAction(node,
                            process.getVersion(), target.getAbsolutePath(), true, statisticsPort, tracePort);
                    camelAction.run(monitor);
                    kars = JMXUtil.installKar(target);
                    writeLog(processMessageManager,
                            new ProcessMessage(MsgType.STD_OUT, "Install kar, return value: " + Arrays.toString(kars) + ".\n"));
                } else {
                    // do nothing
                    return new Status(IStatus.ERROR, "org.talend.designer.esb.runcontainer",
                            "Cannot deply this type into runtime server", null);
                }
                // action.removeTempFilesAfterDeploy();
            } catch (Exception e) {
                e.printStackTrace();
                writeLog(processMessageManager, new ProcessMessage(MsgType.STD_ERR, ExceptionUtils.getStackTrace(e) + ".\n"));
                return new Status(IStatus.ERROR, "org.talend.designer.esb.runcontainer",
                        "Deploy bundle into runtime server failed.", e);
            } finally {
                if (target != null) {
                    // TODO delete target file
                    // target.delete();
                }
            }
            return Status.OK_STATUS;
        }
    }

    public boolean stop() {
        if (null != esbContainerJob) {
            esbContainerJob.unInstall();
        }
        return false;
    }

}
