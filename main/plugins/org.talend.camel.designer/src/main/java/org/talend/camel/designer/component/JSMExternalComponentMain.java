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
package org.talend.camel.designer.component;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.talend.camel.designer.dialog.JMSDialog;

/**
 * @author xpli
 * 
 */
public class JSMExternalComponentMain {

	private JMSExternalComponent connector;

	private JMSDialog dialog;

	public JMSDialog getDialog() {
		return this.dialog;
	}

	public JSMExternalComponentMain(JMSExternalComponent connector) {
		super();
		this.connector = connector;
	}

	public void loadInitialParamters() {
		//
	}

	public Dialog createDialog(Shell parentShell) {
		dialog = new JMSDialog(parentShell, this);
		return dialog;
	}

	public void createUI(Display display) {
		Shell shell = new Shell(display, SWT.APPLICATION_MODAL | SWT.BORDER
				| SWT.RESIZE | SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.TITLE);
		createDialog(shell);
	}


	public JMSExternalComponent getExternalComponent() {
		return this.connector;
	}
}
