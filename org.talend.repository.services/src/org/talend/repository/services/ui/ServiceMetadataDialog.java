package org.talend.repository.services.ui;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;

public class ServiceMetadataDialog extends Dialog {

	private Button samCheck;
	private Button slCheck;
	private Button useCheck;
	private Button basicRadio;
	private Button samlRadio;
	private final ServiceItem serviceItem;

	public ServiceMetadataDialog(IShellProvider parentShell, ServiceItem serviceItem) {
		super(parentShell);
		this.serviceItem = serviceItem;
		
	}

	protected ServiceMetadataDialog(Shell parentShell) {
		super(parentShell);
		serviceItem = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		container.setLayout(new GridLayout(1, false));

		Group samSlGroup = new Group(container, SWT.NONE);
		samSlGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		samSlGroup.setText("ESB Service Features");
		samSlGroup.setLayout(new GridLayout(2, false));

		samCheck = new Button(samSlGroup, SWT.CHECK);
		Label samLabel = new Label(samSlGroup, SWT.NONE);
		samLabel.setText("Use Service Activity Monitor");

		slCheck = new Button(samSlGroup, SWT.CHECK);
		Label slLabel = new Label(samSlGroup, SWT.NONE);
		slLabel.setText("Use Service Locator");

		Group securityGroup = new Group(container, SWT.NONE);
		securityGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		securityGroup.setText("ESB Service Security");
		securityGroup.setLayout(new GridLayout(2, false));

		useCheck = new Button(securityGroup, SWT.CHECK);
		Label useLabel = new Label(securityGroup, SWT.NONE);
		useLabel.setText("Use Service Security");

		basicRadio = new Button(securityGroup, SWT.RADIO);
		final Label basicLabel = new Label(securityGroup, SWT.NONE);
		basicLabel.setText("Username / Password");
		
		samlRadio = new Button(securityGroup, SWT.RADIO);
		final Label samlLabel= new Label(securityGroup, SWT.NONE);
		samlLabel.setText("SAML Token");

		SelectionListener listener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent evt) {;}
			public void widgetSelected(SelectionEvent evt) {
				boolean value = !basicRadio.getEnabled();
				basicRadio.setEnabled(value);
				basicLabel.setEnabled(value);
				samlRadio.setEnabled(value);
				samlLabel.setEnabled(value);
			}
		};
		listener.widgetSelected(null);
		useCheck.addSelectionListener(listener);

		return super.createDialogArea(parent);
	}

	private boolean isUseSam() {
		return samCheck.getSelection();
	}
	
	private boolean isUseSL() {
		return slCheck.getSelection();
	}
	
	private String getSecurity() {
		if (!useCheck.getSelection()) {
			return null;
		}
		if (basicRadio.getSelection()) {
			return "Basic";
		}
		if (samlRadio.getSelection()) {
			return "SAML";
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		ServiceConnection serviceConnection = serviceItem.getServiceConnection();
		EMap<String, String> props = serviceConnection.getAdditionalInfo();
		if (props != null) {
			props.put("UseSAM", Boolean.toString(isUseSam()));
			props.put("UseSL", Boolean.toString(isUseSL()));
			props.put("Security", getSecurity());
		}
		super.okPressed();
	}
		

}

