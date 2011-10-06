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
import org.talend.commons.exception.PersistenceException;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.services.model.services.ServiceItem;
import org.talend.repository.services.model.services.ServicePort;

public class ServiceMetadataDialog extends Dialog {

	private static final String SECURITY = "Security";
	public static final String USE_SL = "UseSL";
	public static final String USE_SAM = "UseSAM";
	private static final String SAML = "SAML";
	private static final String BASIC = "Basic";
	private Button samCheck;
	private Button slCheck;
	private Button useCheck;
	private Button basicRadio;
	private Button samlRadio;
	private final ServiceItem serviceItem;
	private final ServicePort port;
	private boolean useSL;
	private boolean useSAM;
	private String security;

	public ServiceMetadataDialog(IShellProvider parentShell, ServiceItem serviceItem, ServicePort port) {
		super(parentShell);
		this.serviceItem = serviceItem;
		this.port = port;
		EMap<String, String> props = port.getAdditionalInfo();
		if (props != null) {
			useSAM = Boolean.valueOf(props.get(USE_SAM));
			useSL = Boolean.valueOf(props.get(USE_SL));
			security = props.get(SECURITY);
		}
	}

	protected ServiceMetadataDialog(Shell parentShell) {
		super(parentShell);
		serviceItem = null;
		port = null;
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
		samCheck.setSelection(useSAM);
		Label samLabel = new Label(samSlGroup, SWT.NONE);
		samLabel.setText("Use Service Activity Monitor");

		slCheck = new Button(samSlGroup, SWT.CHECK);
		slCheck.setSelection(useSL);
		Label slLabel = new Label(samSlGroup, SWT.NONE);
		slLabel.setText("Use Service Locator");

		
		Group securityGroup = new Group(container, SWT.NONE);
		securityGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		securityGroup.setText("ESB Service Security");
		securityGroup.setLayout(new GridLayout(2, false));

		useCheck = new Button(securityGroup, SWT.CHECK);
		useCheck.setSelection(null != security);
		Label useLabel = new Label(securityGroup, SWT.NONE);
		useLabel.setText("Use Service Security");

		basicRadio = new Button(securityGroup, SWT.RADIO);
		basicRadio.setSelection(BASIC.equals(security));
		final Label basicLabel = new Label(securityGroup, SWT.NONE);
		basicLabel.setText("Username / Password");
		
		samlRadio = new Button(securityGroup, SWT.RADIO);
		samlRadio.setSelection(SAML.equals(security));
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
		if ((null == security)) {
			listener.widgetSelected(null);
		}
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
			return BASIC;
		}
		if (samlRadio.getSelection()) {
			return SAML;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		EMap<String, String> props = port.getAdditionalInfo();
		if (props != null) {
			props.put(USE_SAM, Boolean.toString(isUseSam()));
			props.put(USE_SL, Boolean.toString(isUseSL()));
			props.put(SECURITY, getSecurity());
		}
		
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        try {
            factory.save(serviceItem);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
		super.okPressed();
	}
		

}

