// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.exception.SystemException;
import org.talend.commons.utils.VersionUtils;
import org.talend.commons.utils.generation.JavaUtils;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.IService;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ProcessItem;
import org.talend.core.model.properties.RoutineItem;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.ui.branding.AbstractBrandingService;
import org.talend.core.ui.branding.IBrandingService;
import org.talend.designer.codegen.AbstractRoutineSynchronizer;
import org.talend.designer.codegen.ITalendSynchronizer;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.RepositoryNodeUtilities;

/**
 * DOC guanglong.du class global comment. Detailled comment
 */
public class CamelJavaRoutesSychronizer extends AbstractRoutineSynchronizer {

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.ITalendSynchronizer#syncAllRoutines()
     */
    @Override
    public void syncAllRoutines() throws SystemException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see
     * org.talend.designer.codegen.ITalendSynchronizer#deleteBeanfile(org.talend.core.model.repository.IRepositoryViewObject
     * )
     */
    @Override
    public void deleteBeanfile(IRepositoryViewObject objToDelete) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.designer.codegen.AbstractRoutineSynchronizer#doSyncBean(org.talend.core.model.properties.Item,
     * boolean)
     */
    @Override
    protected void doSyncRoutine(RoutineItem routineItem, boolean copyToTemp) throws SystemException {
        FileOutputStream fos = null;
        try {
            IFile file = getRoutineFile(routineItem);
            if (file == null) {
                return;
            }
            if (routineItem.getProperty().getModificationDate() != null) {
                long modificationItemDate = routineItem.getProperty().getModificationDate().getTime();
                long modificationFileDate = file.getModificationStamp();
                if (modificationItemDate <= modificationFileDate) {
                    return;
                }
            } else {
                routineItem.getProperty().setModificationDate(new Date());
            }

            if (copyToTemp) {
                String beanContent = new String(routineItem.getContent().getInnerContent());
                // see 14713
                String version = VersionUtils.getVersion();
                if (beanContent.contains("%GENERATED_LICENSE%")) { //$NON-NLS-1$
                    IService service = GlobalServiceRegister.getDefault().getService(IBrandingService.class);
                    if (service instanceof AbstractBrandingService) {
                        String routineHeader = ((AbstractBrandingService) service).getRoutineLicenseHeader(version);
                        beanContent = beanContent.replace("%GENERATED_LICENSE%", routineHeader); //$NON-NLS-1$
                    }
                }// end
                String label = routineItem.getProperty().getLabel();
                if (!label.equals(ITalendSynchronizer.BEAN_TEMPLATE) && beanContent != null) {
                    beanContent = beanContent.replaceAll(ITalendSynchronizer.BEAN_TEMPLATE, label);
                    File f = file.getLocation().toFile();
                    fos = new FileOutputStream(f);
                    fos.write(beanContent.getBytes());
                    fos.close();
                }
            }
            file.refreshLocal(1, null);
        } catch (CoreException e) {
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                // ignore me even if i'm null
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
     * org.talend.designer.codegen.ITalendSynchronizer#getRoutinesFile(org.talend.core.model.properties.RoutineItem)
     */
    @Override
    public IFile getRoutinesFile(Item item) throws SystemException {
        try {
            if (item instanceof BeanItem) {
                BeanItem routineItem = (BeanItem) item;
                ProjectManager projectManager = ProjectManager.getInstance();
                org.talend.core.model.properties.Project project = projectManager.getProject(routineItem);
                IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(project.getTechnicalLabel());
                String repositoryPath = ERepositoryObjectType.getFolderName(CamelRepositoryNodeType.repositoryBeansType);
                String folderPath = RepositoryNodeUtilities.getPath(routineItem.getProperty().getId()).toString();
                String fileName = routineItem.getProperty().getLabel() + '_' + routineItem.getProperty().getVersion()
                        + JavaUtils.ITEM_EXTENSION;
                String path = null;
                if (folderPath != null && folderPath.trim().length() > 0) {
                    path = repositoryPath + '/' + folderPath + '/' + fileName;
                } else {
                    path = repositoryPath + '/' + fileName;
                }

                IFile file = iProject.getFile(path);
                return file;
            }

        } catch (Exception e) {
            throw new SystemException(e);
        }

        return null;
    }

}
