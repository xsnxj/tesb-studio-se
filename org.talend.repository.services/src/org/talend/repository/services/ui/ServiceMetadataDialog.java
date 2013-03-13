package org.talend.repository.services.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.services.model.services.ServiceConnection;
import org.talend.repository.services.model.services.ServiceItem;

public class ServiceMetadataDialog extends Dialog {

    public static final String SECURITY_BASIC = "Security.Basic";
    public static final String SECURITY_SAML = "Security.SAML";
    public static final String AUTHORIZATION = "Authorization";    
    public static final String USE_SERVICE_REGISTRY = "UseServiceRegisrty";    
    public static final String USE_SAM = "UseSAM";
    public static final String USE_SL = "UseSL";
    public static final String SL_CUSTOM_PROP_PREFIX = "slCustomProperty_";

    private final ServiceItem serviceItem;
    private final ServiceConnection serviceConnection;
    private ServiceMetadataCustomPropertiesTable customPropertiesTable;
    private boolean useSAM;
    private boolean useSL;
    private boolean useServiceRegistry;    
    private Map<String, String> slCustomProperties = new HashMap<String, String>();
    private boolean securityBasic;
	private boolean securitySAML;
	private boolean authorization;	

    public ServiceMetadataDialog(IShellProvider parentShell, ServiceItem serviceItem, ServiceConnection serviceConnection) {
        super(parentShell);
        this.serviceItem = serviceItem;
        this.serviceConnection = serviceConnection;
        EMap<String, String> props = serviceConnection.getAdditionalInfo();
        if (props != null) {
            useSAM = Boolean.valueOf(props.get(USE_SAM));
            useSL = Boolean.valueOf(props.get(USE_SL));
            securitySAML = Boolean.valueOf(props.get(SECURITY_SAML));
            securityBasic = Boolean.valueOf(props.get(SECURITY_BASIC));
            authorization = Boolean.valueOf(props.get(AUTHORIZATION));  
            useServiceRegistry = Boolean.valueOf(props.get(USE_SERVICE_REGISTRY));

            for (Map.Entry<String, String> prop : props.entrySet()) {
                if (prop.getKey().startsWith(SL_CUSTOM_PROP_PREFIX)) {
                    slCustomProperties.put(prop.getKey().substring(SL_CUSTOM_PROP_PREFIX.length()),
                            prop.getValue());
                }
            }
        }
    }

    protected ServiceMetadataDialog(Shell parentShell) {
        super(parentShell);
        serviceItem = null;
        serviceConnection = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        container.setLayout(new GridLayout());

        Group samSlGroup = new Group(container, SWT.NONE);
        samSlGroup.setText("ESB Service Features");
        samSlGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        samSlGroup.setLayout(new GridLayout());

        final Button samCheck = new Button(samSlGroup, SWT.CHECK);
        samCheck.setText("Use Service Activity Monitor");
        samCheck.setSelection(useSAM);
        samCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                useSAM = samCheck.getSelection();
            }
        });

        final Button slCheck = new Button(samSlGroup, SWT.CHECK);
        slCheck.setText("Use Service Locator");
        slCheck.setSelection(useSL);
        slCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                useSL = slCheck.getSelection();
                customPropertiesTable.setEditable(slCheck.getSelection());
            }
        });

        customPropertiesTable = new ServiceMetadataCustomPropertiesTable(samSlGroup, slCustomProperties);
        customPropertiesTable.setEditable(useSL);

        final Group securityGroup = new Group(container, SWT.NONE);
        securityGroup.setText("ESB Service Security");
        securityGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        securityGroup.setLayout(new GridLayout());

        final Button useSR = new Button(container, SWT.CHECK);
        final Button basicCheck = new Button(securityGroup, SWT.CHECK);
        final Button samlCheck = new Button(securityGroup, SWT.CHECK);
        final Button authorizationCheck = new Button(securityGroup, SWT.CHECK);
        
        
        useSR.setText("Use Service Registry");
        useSR.setSelection(useServiceRegistry);
        useSR.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	useServiceRegistry = useSR.getSelection();
            	securityGroup.setEnabled(!useSR.getSelection());
            	authorizationCheck.setEnabled(!useSR.getSelection() && (samlCheck.getSelection() || basicCheck.getSelection()) );
            	samlCheck.setEnabled(!useSR.getSelection());            	
            	basicCheck.setEnabled(!useSR.getSelection());            	
            }
        });
        
        basicCheck.setText("Username / Password");
        basicCheck.setSelection(securityBasic);
    	basicCheck.setEnabled(!useSR.getSelection());        
        basicCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                securityBasic = basicCheck.getSelection();
                authorizationCheck.setEnabled(samlCheck.getSelection() || basicCheck.getSelection());
            }
        });


        samlCheck.setText("SAML Token");
        samlCheck.setSelection(securitySAML);
    	samlCheck.setEnabled(!useSR.getSelection());        
        samlCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                securitySAML = samlCheck.getSelection();
                authorizationCheck.setEnabled(samlCheck.getSelection() || basicCheck.getSelection());
            }
        });
        
        
        authorizationCheck.setText("Authorization");
        authorizationCheck.setSelection(authorization);
        authorizationCheck.setEnabled(!useSR.getSelection() && (samlCheck.getSelection() || basicCheck.getSelection()));        
        authorizationCheck.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
            	authorization = authorizationCheck.getSelection();
            }
        });
        
        
        return super.createDialogArea(parent);
    }

    private boolean isUseSam() {
        return useSAM;
    }

    private boolean isUseSL() {
        return useSL;
    }

    private boolean getSecurityBasic() {
        return securityBasic;
    }

    private boolean getSecuritySAML() {
        return securitySAML;
    }

    private boolean getAuthorization() {
        return authorization;
    }

    private boolean getUseServiceRegistry() {
        return useServiceRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    @Override
    protected void okPressed() {
        EMap<String, String> props = serviceConnection.getAdditionalInfo();
        if (props != null) {
            props.put(USE_SAM, Boolean.toString(isUseSam()));
            props.put(USE_SL, Boolean.toString(isUseSL()));
            props.put(SECURITY_BASIC, Boolean.toString(getSecurityBasic()));
            props.put(SECURITY_SAML, Boolean.toString(getSecuritySAML()));
            props.put(AUTHORIZATION, Boolean.toString(getAuthorization()));            
            props.put(USE_SERVICE_REGISTRY, Boolean.toString(getUseServiceRegistry()));            

            if (isUseSL()) {
                slCustomProperties = new HashMap<String, String>(customPropertiesTable
                        .getPropertiesList().getPropertiesMap());
                // remove old custom properties
                List<String> props2delete = new ArrayList<String>();
                for (String propKey : props.keySet()) {
                    if (propKey.startsWith(SL_CUSTOM_PROP_PREFIX)) {
                        props2delete.add(propKey);
                    }
                }
                for (String propKey : props2delete) {
                    props.removeKey(propKey);
                }
                // set new custom properties
                for (Map.Entry<String, String> prop : slCustomProperties.entrySet()) {
                    props.put(SL_CUSTOM_PROP_PREFIX + prop.getKey(), prop.getValue());
                }
            }
        }

        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        try {
            factory.save(serviceItem);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        super.okPressed();
    }

    protected Point getInitialSize() {
        Point pt = super.getInitialSize();
        pt.x = Math.max(pt.x, 300);
        pt.y = Math.max(pt.y, 300);
        return pt;
    }

    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }
}
