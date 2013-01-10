// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.spring.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.util.CamelRepositoryNodeType;
import org.talend.commons.ui.swt.actions.ITreeContextualAction;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.spring.ui.CamelSpringUIPlugin;
import org.talend.designer.camel.spring.ui.i18n.Messages;
import org.talend.designer.camel.spring.ui.wizards.ExportSpringXMLWizard;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;
import org.talend.repository.ProjectManager;
import org.talend.repository.model.BinRepositoryNode;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.ui.actions.AContextualAction;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class ExportAsSpringContextualAction extends AContextualAction implements
		ITreeContextualAction {

	public ExportAsSpringContextualAction() {
		super();
		this.setText(Messages
				.getString("ExportAsSpringContextualAction_actionText")); //$NON-NLS-1$
		this.setToolTipText(Messages
				.getString("ExportAsSpringContextualAction_actionTooltip")); //$NON-NLS-1$
		this.setImageDescriptor(CamelSpringUIPlugin.getDefault()
				.getImageDescriptor("icons/export.gif")); //$NON-NLS-1$
	}

	public void init(TreeViewer viewer, IStructuredSelection selection) {
		setEnabled(false);
		if (selection.size() != 1) {
			return;
		}
		IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
		if (factory.isUserReadOnlyOnCurrentProject()) {
			return;
		}
		Object o = selection.getFirstElement();
		RepositoryNode node = (RepositoryNode) o;
		switch (node.getType()) {
		case REPOSITORY_ELEMENT:
			if (node.getObjectType() != CamelRepositoryNodeType.repositoryRoutesType) {
				return;
			}
			break;
		default:
			return;
		}
		RepositoryNode parent = node.getParent();
		if (parent != null && parent instanceof BinRepositoryNode) {
			return;
		}
		if (!ProjectManager.getInstance().isInCurrentMainProject(node)) {
			return;
		}

		// If the editProcess action canwork is true, then detect that the job
		// version is the latest verison or not.
		if (!isLastVersion(node)) {
			return;
		}

		setEnabled(true);
	}

	@Override
	protected void doRun() {
	    
	    IStructuredSelection  selection = (IStructuredSelection) this.getSelection();
	    Object obj = selection.getFirstElement();
	    RepositoryNode node = (RepositoryNode) obj;
	    Property property = (Property) node.getObject().getProperty();
	    CamelProcessItem processItem = (CamelProcessItem) property.getItem();
	    ProcessType process = processItem.getProcess();
	    
	    ExportSpringXMLWizard exportSpringXMLWizard = new ExportSpringXMLWizard(process);
	    WizardDialog dlg = new WizardDialog(Display.getCurrent()
				.getActiveShell(), exportSpringXMLWizard);
		dlg.open();
	}

}
