// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.commons.exception.SystemException;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.commons.utils.workbench.resources.ResourceUtils;
import org.talend.core.CorePlugin;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.general.ILibrariesService;
import org.talend.core.model.general.Project;
import org.talend.core.model.process.JobInfo;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.RoutineItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.utils.PerlResourcesHelper;
import org.talend.core.ui.branding.AbstractBrandingService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.codegen.AbstractRoutineSynchronizer;
import org.talend.designer.runprocess.IRunProcessService;
import org.talend.repository.model.RepositoryConstants;
import org.talend.repository.model.ResourceModelUtils;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelPerlBeanSychronizer extends AbstractRoutineSynchronizer {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#syncAllRoutines()
     */
    public void syncAllRoutines() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#syncAllBeans()
     */
    public void syncAllBeans() throws SystemException {
        // TODO Auto-generated method stub
        for (IRepositoryViewObject routine : getRoutines()) {
            RoutineItem routineItem = (RoutineItem) routine.getProperty().getItem();
            if (!routineItem.isBuiltIn()) {
                syncRoutine(routineItem, false);
            }
        }
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#getFile(org.talend.core.model.properties.Item)
     */
    public IFile getFile(Item item) throws SystemException {
        if (item instanceof BeanItem) {
            return getBeanFile((BeanItem) item);
        } else if (item instanceof CamelProcessItem) {
            return getCamelProcessFile((CamelProcessItem) item);
        }
        return null;
    }

    private IFile getBeanFile(Item beanItem) throws SystemException {
        IRunProcessService service = CamelDesignerPlugin.getDefault().getRunProcessService();
        Project project = ((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY)).getProject();

        IProject fsProject = ResourceModelUtils.getProject(project);
        IFolder folder = ResourceUtils.getFolder(fsProject, RepositoryConstants.TEMP_DIRECTORY, true);
        IFile tempfile = ResourceUtils.getFile(folder, "tempRoutine" + beanItem.getProperty().getId(), false); //$NON-NLS-1$

        return tempfile;
    }

    private IFile getCamelProcessFile(CamelProcessItem processItem) throws SystemException {
        IFile tempfile = null;
        IRunProcessService service = CamelDesignerPlugin.getDefault().getRunProcessService();
        try {
            IProject perlProject = service.getProject(ECodeLanguage.PERL);
            String rootProjectName = PerlResourcesHelper.getRootProjectName(processItem);
            tempfile = perlProject.getFile(PerlResourcesHelper.getJobFileName(rootProjectName, processItem.getProperty()
                    .getLabel(), processItem.getProperty().getVersion()));
        } catch (CoreException e) {
            throw new SystemException(e);
        }

        return tempfile;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#getProcessFile(org.talend.core.model.process.JobInfo)
     */
    public IFile getProcessFile(JobInfo jobInfo) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.designer.codegen.ITalendSynchronizer#deleteBeanfile(org.talend.core.model.repository.IRepositoryViewObject
     * )
     */
    public void deleteBeanfile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.designer.codegen.AbstractRoutineSynchronizer#doSyncRoutine(org.talend.core.model.properties.RoutineItem
     * , boolean)
     */
    @Override
    protected void doSyncRoutine(RoutineItem routineItem, boolean copyToTemp) throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#doSyncBean(org.talend.core.model.properties.Item,
     * boolean)
     */
    @Override
    protected void doSyncBean(Item item, boolean copyToTemp) throws SystemException {
        if (item instanceof BeanItem) {
            BeanItem beanItem = (BeanItem) item;
            ByteArrayInputStream byteArrayInputStream = null;
            try {
                IRunProcessService service = CamelDesignerPlugin.getDefault().getRunProcessService();
                Project project = ((RepositoryContext) CorePlugin.getContext().getProperty(Context.REPOSITORY_CONTEXT_KEY))
                        .getProject();

                // see 14713
                String routineContents = new String(beanItem.getContent().getInnerContent());
                String version = beanItem.getProperty().getVersion();
                if (routineContents.contains("%GENERATED_LICENSE%")) { //$NON-NLS-1$
                    String routineHeader = ((AbstractBrandingService) GlobalServiceRegister.getDefault().getService(
                            IBrandingService.class)).getRoutineLicenseHeader(version);
                    routineContents = routineContents.replace("%GENERATED_LICENSE%", routineHeader); //$NON-NLS-1$
                    if (routineContents.contains("//")) { //$NON-NLS-1$
                        routineContents = routineContents.replace("//", "#").replace("#www", "//www"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    }
                }// end

                // if (!beanItem.isBuiltIn()) {
                // Copy the routine in external "lib/perl" folder:
                String librariesPath = CorePlugin.getDefault().getLibrariesService().getLibrariesPath() + IPath.SEPARATOR
                        + ILibrariesService.SOURCE_PERL_ROUTINES_FOLDER + IPath.SEPARATOR + project.getTechnicalLabel()
                        + IPath.SEPARATOR + beanItem.getProperty().getLabel() + service.getRoutineFilenameExt();
                File target = new File(librariesPath);
                byteArrayInputStream = new ByteArrayInputStream(routineContents.getBytes());
                FilesUtils.copyFile(byteArrayInputStream, target);
                // }

                IResource tempfile = getBeanFile(beanItem);

                if (copyToTemp) {
                    beanItem.getContent().setInnerContent(routineContents.getBytes());
                    beanItem.getContent().setInnerContentToFile(tempfile.getLocation().toFile());
                }
                tempfile.refreshLocal(1, null);
            } catch (CoreException e) {
                throw new SystemException(e);
            } catch (IOException e) {
                throw new SystemException(e);
            } finally {
                try {
                    byteArrayInputStream.close();
                } catch (Exception e) {
                    // ignore me even if i'm null
                }
            }
        }
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#deleteRoutinefile(org.talend.core.model.repository.
     * IRepositoryViewObject)
     */
    @Override
    public void deleteRoutinefile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#renameRoutineClass(org.talend.core.model.properties.
     * RoutineItem)
     */
    @Override
    public void renameRoutineClass(RoutineItem routineItem) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.designer.codegen.AbstractRoutineSynchronizer#renameBeanClass(org.talend.core.model.properties.Item)
     */
    @Override
    public void renameBeanClass(Item beanItem) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.designer.codegen.ITalendSynchronizer#getRoutinesFile(org.talend.core.model.properties.RoutineItem)
     */
    public IFile getRoutinesFile(Item routineItem) throws SystemException {
        // TODO Auto-generated method stub
        return null;
    }

}
