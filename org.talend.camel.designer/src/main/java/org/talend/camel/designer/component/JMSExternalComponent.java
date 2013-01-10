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
package org.talend.camel.designer.component;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.process.AbstractExternalNode;
import org.talend.core.model.process.IComponentDocumentation;
import org.talend.core.model.process.IExternalData;
import org.talend.designer.core.model.components.EParameterName;

/**
 * @author
 * 
 */
public class JMSExternalComponent extends AbstractExternalNode {

	private JSMExternalComponentMain main;

	public void initialize() {
		main = new JSMExternalComponentMain(this);

	}

	public int open(Display display) {
		this.getElementParameter(EParameterName.UPDATE_COMPONENTS.getName())
				.setValue(Boolean.TRUE);
		Dialog dialog = main.createDialog(display.getActiveShell());
		if (dialog.open() == IDialogConstants.OK_ID) {
			return SWT.OK;
		}
		return -1;
	}

	public int open(Composite parent) {
		this.getElementParameter(EParameterName.UPDATE_COMPONENTS.getName())
				.setValue(Boolean.TRUE);

		return open(parent.getDisplay());
	}

	public void setExternalData(IExternalData persistentData) {
		// TODO Auto-generated method stub

	}

	public void renameInputConnection(String oldName, String newName) {
		// TODO Auto-generated method stub

	}

	public void renameOutputConnection(String oldName, String newName) {
		// TODO Auto-generated method stub

	}

	public IComponentDocumentation getComponentDocumentation(
			String componentName, String tempFolderPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public IExternalData getTMapExternalData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void renameMetadataColumnName(String conectionName,
			String oldColumnName, String newColumnName) {
		// TODO Auto-generated method stub

	}

}
