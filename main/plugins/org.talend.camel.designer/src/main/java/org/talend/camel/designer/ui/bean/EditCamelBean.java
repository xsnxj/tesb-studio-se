// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.ui.bean;

import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;
import org.talend.camel.core.model.camelProperties.BeanItem;
import org.talend.camel.designer.i18n.Messages;
import org.talend.camel.designer.util.ECamelCoreImage;
import org.talend.camel.model.CamelRepositoryNodeType;
import org.talend.commons.exception.SystemException;
import org.talend.commons.runtime.model.repository.ERepositoryStatus;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.core.CorePlugin;
import org.talend.core.model.components.IComponent;
import org.talend.core.model.general.ModuleNeeded;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.ui.component.ComponentsFactoryProvider;
import org.talend.designer.core.model.utils.emf.component.impl.ComponentFactoryImpl;
import org.talend.designer.core.model.utils.emf.component.impl.IMPORTTypeImpl;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 * $Id: EditProcess.java 52559 2010-12-13 04:14:06Z nrousseau $
 * 
 */
public class EditCamelBean extends AbstractBeanAction implements IIntroAction {

    private String EDIT_LABEL = Messages.getString("EditProcess.editBean"); //$NON-NLS-1$

    //    private String OPEN_LABEL = Messages.getString("EditProcess.openBean"); //$NON-NLS-1$

    // private static final String DBPROJECT_LABEL = "teneo";

    // private Properties params;

    public EditCamelBean() {
        super();
        this.setText(EDIT_LABEL);
        this.setToolTipText(EDIT_LABEL);
        this.setImageDescriptor(ImageProvider.getImageDesc(ECamelCoreImage.BEAN_ICON));
    }

    @Override
    public void init(TreeViewer viewer, IStructuredSelection selection) {
        super.init(viewer, selection);
        boolean canWork = !selection.isEmpty() && selection.size() == 1;
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        if (factory.isUserReadOnlyOnCurrentProject()) {
            canWork = false;
        }
        RepositoryNode node = (RepositoryNode) selection.getFirstElement();
        if (canWork
                && (node.getObjectType() != CamelRepositoryNodeType.repositoryBeansType
                        || !ProjectManager.getInstance().isInCurrentMainProject(node) || !isLastVersion(node))) {
            canWork = false;
        }
        if (canWork) {
            canWork = factory.getStatus(node.getObject()) != ERepositoryStatus.DELETED;
        }
        setEnabled(canWork);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    protected void doRun() {
        if (repositoryNode == null) {
            repositoryNode = (RepositoryNode) ((IStructuredSelection) getSelection()).getFirstElement();
        }
        BeanItem beanItem = (BeanItem) repositoryNode.getObject().getProperty().getItem();

        byte[] ct = beanItem.getContent().getInnerContent();

        String contentString = new String(ct);

        if (contentString.contains("org.apache.camel.")) {
            Pattern p = Pattern.compile("camel-core-\\d+(.\\d+)*(\\S+)*(\\.jar)$");

            if (beanItem.getImports().size() == 0) {
                addCamelDependency(beanItem);
            }

            for (int i = 0; i < beanItem.getImports().size(); i++) {
                Object o = beanItem.getImports().get(i);

                if (o instanceof IMPORTTypeImpl) {
                    IMPORTTypeImpl importType = (IMPORTTypeImpl) o;
                    if (p.matcher(importType.getMODULE()).matches()) {
                        continue;
                    }
                } else {
                    addCamelDependency(beanItem);
                }

            }

        }

        try {
            openBeanEditor(beanItem, false);
            refresh(repositoryNode);
            CorePlugin.getDefault().getRunProcessService().updateLibraries(beanItem);
        } catch (PartInitException e) {
            MessageBoxExceptionHandler.process(e);
        } catch (SystemException e) {
            MessageBoxExceptionHandler.process(e);
        }
    }

    private void addCamelDependency(BeanItem beanItem) {
        IMPORTTypeImpl camelImport = (IMPORTTypeImpl) ComponentFactoryImpl.eINSTANCE.createIMPORTType();
        IComponent component = ComponentsFactoryProvider.getInstance().get("cTimer", "CAMEL");
        ModuleNeeded cmn = null;
        List<ModuleNeeded> mns = component.getModulesNeeded();

        for (ModuleNeeded mn : mns) {
            if (mn.getId().equals("camel-core")) {
                cmn = mn;
                break;
            }
        }
        camelImport.setMODULE(cmn.getModuleName());
        camelImport.setMVN(cmn.getMavenUri());
        camelImport.setREQUIRED(true);
        beanItem.getImports().add(camelImport);
    }

    @Override
    public Class<?> getClassForDoubleClick() {
        return BeanItem.class;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.ui.intro.config.IIntroAction#run(org.eclipse.ui.intro.IIntroSite, java.util.Properties)
     */
    @Override
    public void run(IIntroSite site, Properties params) {
        // this.params = params;
        PlatformUI.getWorkbench().getIntroManager().closeIntro(PlatformUI.getWorkbench().getIntroManager().getIntro());
        doRun();

    }
}
