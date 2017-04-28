package org.talend.camel.designer.generator;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.talend.commons.ui.runtime.image.ImageProvider;
import org.talend.commons.ui.swt.tableviewer.TableViewerCreator;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.model.general.INexusService;
import org.talend.core.model.process.IElementParameter;
import org.talend.core.ui.CoreUIPlugin;
import org.talend.core.ui.properties.tab.IDynamicProperty;
import org.talend.designer.core.ui.editor.properties.controllers.AbstractElementPropertySectionController;

public class ConfigOptionController extends AbstractElementPropertySectionController {

	protected final String JAR_NAME = "JAR_NAME";
	protected final String JAR_VERSION = "JAR_VERSION";
	protected final String JAR_NEXUS_VERSION = "JAR_NEXUS_VERSION";
	protected final String JAR_NEXUS_PRE_VERSION = "JAR_NEXUS_PRE_VERSION";
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");//20170411034415
	
	protected INexusService service;
	
	public ConfigOptionController(IDynamicProperty dp) {
		super(dp);
        if (GlobalServiceRegister.getDefault().isServiceRegistered(INexusService.class)) {
        	service = (INexusService) GlobalServiceRegister.getDefault().getService(
        			INexusService.class);
        }
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

	}

    @Override
    public Control createControl(Composite subComposite, IElementParameter param, int numInRow, int nbInRow, int top,
            Control lastControl) {
        Button theBtn = getWidgetFactory().createButton(subComposite, "", SWT.PUSH); //$NON-NLS-1$
        theBtn.setBackground(subComposite.getBackground());
        if (param.getDisplayName().equals("")) { //$NON-NLS-1$
            theBtn.setImage(ImageProvider.getImage(CoreUIPlugin.getImageDescriptor(DOTS_BUTTON)));
        } else {
            theBtn.setText(param.getDisplayName());
        }
        FormData data = new FormData();
        if (isInWizard()) {
            if (lastControl != null) {
                data.right = new FormAttachment(lastControl, 0);
            } else {
                data.right = new FormAttachment(100, -ITabbedPropertyConstants.HSPACE);
            }
        } else {
            if (lastControl != null) {
                data.left = new FormAttachment(lastControl, 0);
            } else {
                data.left = new FormAttachment((((numInRow - 1) * MAX_PERCENT) / nbInRow), 0);
            }
        }
        data.top = new FormAttachment(0, top);
        theBtn.setLayoutData(data);
        theBtn.setEnabled(!param.isReadOnly());
        theBtn.setData(param);
        hashCurControls.put(param.getName(), theBtn);
        theBtn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Command cmd = createCommand((Button) e.getSource());
                executeCommand(cmd);
            }
        });
        Point initialSize = theBtn.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        dynamicProperty.setCurRowSize(initialSize.y + ITabbedPropertyConstants.VSPACE);
        return theBtn;
    }

	protected Command createCommand(Button source) {
		return null;
	}

	@Override
	public int estimateRowSize(Composite subComposite, IElementParameter param) {
		return 0;
	}

	@Override
	public void refresh(IElementParameter param, boolean check) {

		TableViewerCreator tableViewerCreator = (TableViewerCreator) hashCurControls.get("NEED_UPDATE_LIST");
		
        if (tableViewerCreator == null || tableViewerCreator.getTable() == null || tableViewerCreator.getTable().isDisposed()) {
            return;
        }else{
        	tableViewerCreator.getTableViewer().refresh();
        }
	}
	
    protected Map getNeedUpdateJar(String flag,String jarName,String nexusVersion,String nexusPreVersion){
		Map needUpdateJar = new HashMap();
		
		needUpdateJar.put("SCHEMA_COLUMN", flag);
		
		needUpdateJar.put(JAR_NAME, jarName);
		
		needUpdateJar.put(JAR_NEXUS_VERSION, nexusVersion);
		
		needUpdateJar.put(JAR_NEXUS_PRE_VERSION, nexusPreVersion);
		
		return needUpdateJar;
    }

}
