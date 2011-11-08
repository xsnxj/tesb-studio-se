package org.talend.repository.services;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.talend.repository.services.messages"; //$NON-NLS-1$

	public static String ExportServiceAction_Action_Label;
	public static String ServiceExportManager_Exception_cannot_create_document_builder;
	public static String ServiceExportManager_Exception_cannot_open_file;
	public static String ServiceExportManager_Exception_cannot_serialize_xml;
	public static String ServiceExportManager_Exception_Cannot_parse_job_xml;
	public static String ServiceExportManager_Exception_invalid_blueprint_xml;
	public static String ServiceExportWizard_Wizard_Title;

	public static String AssignWsdlDialog_Title;
	public static String AssignWsdlDialog_Description;
	public static String AssignWsdlDialog_Choice_CreateNew;
	public static String AssignWsdlDialog_Choice_ImportExistent;
	public static String AssignWsdlDialog_ImportExistent_FilePath;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

}
