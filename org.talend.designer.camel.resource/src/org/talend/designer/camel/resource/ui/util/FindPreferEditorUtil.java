package org.talend.designer.camel.resource.ui.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.editors.ResourceEditorListener;
import org.talend.designer.camel.resource.editors.RouteResourceEditor;
import org.talend.designer.camel.resource.editors.RouteResoureChangeListener;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;
import org.talend.repository.model.IRepositoryNode;

public class FindPreferEditorUtil {

	private static final String JAVA_EDITOR_ID = "org.eclipse.jdt.ui.CompilationUnitEditor";

	private static final String WSDL_EDITOR_ID = "org.eclipse.wst.wsdl.ui.internal.WSDLEditor";

	private static final String XML_EDITOR_ID = "org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart";

	private static FindPreferEditorUtil INSTANCE = new FindPreferEditorUtil();

	// private static final String EDITOR_EXTENSION_POINT =
	// "org.eclipse.ui.editors";
	// private static final String CONTENTTYPE_EXTENSION_POINT =
	// "org.eclipse.core.contenttype.contentTypes";
	//
	// private IConfigurationElement[] editorExtensions;
	// private IConfigurationElement[] contentTypeExtensions;

	private Map<String, String> predefinedMap = new HashMap<String, String>();

	private FindPreferEditorUtil() {
		initialize();
	}

	private void initialize() {
		// IExtensionRegistry extensionRegistry =
		// Platform.getExtensionRegistry();
		// editorExtensions = extensionRegistry
		// .getConfigurationElementsFor(EDITOR_EXTENSION_POINT);
		// contentTypeExtensions = extensionRegistry
		// .getConfigurationElementsFor(CONTENTTYPE_EXTENSION_POINT);

		predefinedMap.put("xml", XML_EDITOR_ID);
		predefinedMap.put("wsdl", WSDL_EDITOR_ID);
		predefinedMap.put("java", JAVA_EDITOR_ID);
	}

	/**
	 * find a prefer editor from all existing editors according to the
	 * fileExtensions
	 * 
	 * @param fileExtension
	 * @return
	 */
	private String findPreferEditor(String fileExtension) {
		String editorId = RouteResourceEditor.ID;
		if (fileExtension == null || fileExtension.trim().equals("")) {
			return editorId;
		}
		String found = predefinedMap.get(fileExtension.toLowerCase());
		if (found != null) {
			return found;
		}

		IEditorDescriptor defaultEditor = PlatformUI.getWorkbench()
				.getEditorRegistry().getDefaultEditor("*." + fileExtension);
		if (defaultEditor != null)
			return defaultEditor.getId();
		return editorId;
		// for (IConfigurationElement e : editorExtensions) {
		// String id = e.getAttribute("id");
		// // only care about eclipse extensions
		// if (id == null || !id.startsWith("org.eclipse")) {
		// continue;
		// }
		//
		// String supportedExtensions = e.getAttribute("extensions");
		//
		// if (supportedExtensions == null || supportedExtensions.equals("")) {
		// /*
		// * if there's no "extensions" attribute defined in Editor
		// * extension declaration then, search from "contentTypeBinding"
		// * attribute if exist
		// */
		// IConfigurationElement[] bindingContentTypes = e
		// .getChildren("contentTypeBinding");
		// if (bindingContentTypes == null
		// || bindingContentTypes.length == 0
		// || contentTypeExtensions == null) {
		// continue;
		// }
		//
		// for (IConfigurationElement binding : bindingContentTypes) {
		// String contentTypeId = binding
		// .getAttribute("contentTypeId");
		// for (IConfigurationElement contentType : contentTypeExtensions) {
		// if (!"content-type".equals(contentType.getName())) {
		// continue;
		// }
		// if (contentTypeId.endsWith(contentType.getAttribute("id"))
		// && fileExtension.equals(contentType
		// .getAttribute("file-extensions"))) {
		// return id;
		// }
		// }
		// }
		//
		// } else {
		// /*
		// * search from "extensions" defined in Editor extension
		// * declaration
		// */
		// String[] extensions = supportedExtensions.split(",");
		// for (String s : extensions) {
		// if (fileExtension.equals(s.trim())) {
		// return id;
		// }
		// }
		// }
		// }
		// return editorId;
	}
	
	/**
	 * Open default editor
	 * 
	 * @param page
	 * @param node
	 * @param item
	 */
	public static void openDefaultEditor(final IWorkbenchPage page,
			IRepositoryNode node, RouteResourceItem item) {
		openEditor(page, node, item, RouteResourceEditor.ID);
	}

	/**
	 * Open or bind Route resource editor.
	 * 
	 * @param page
	 * @param node
	 * @param item
	 */
	public static void openEditor(final IWorkbenchPage page,
			IRepositoryNode node, RouteResourceItem item) {
		openEditor(page, node, item,
				FindPreferEditorUtil.INSTANCE.findPreferEditor(item
						.getBindingExtension()));
	}

	/**
	 * Open or bind Route resource editor by specifing editorID
	 * 
	 * @param page
	 * @param node
	 * @param item
	 */
	public static void openEditor(final IWorkbenchPage page,
			IRepositoryNode node, RouteResourceItem item, String editorId) {
		try {

			RouteResourceInput fileEditorInput = RouteResourceInput
					.createInput(item);

			fileEditorInput.setRepositoryNode(node);

			IEditorPart editorPart = page.findEditor(fileEditorInput);

			page.getWorkbenchWindow()
					.getPartService()
					.addPartListener(
							new ResourceEditorListener(fileEditorInput, page));

			if (!RouteResourceEditor.ID.endsWith(editorId)) {
				ResourcesPlugin.getWorkspace().addResourceChangeListener(
						new RouteResoureChangeListener(fileEditorInput));
			}

			if (editorPart == null) {
				editorPart = page.openEditor(fileEditorInput, editorId, true);

			} else {
				editorPart = page.openEditor(fileEditorInput, editorId);
			}

		} catch (Exception e) {
			try {
				ProxyRepositoryFactory.getInstance().unlock(item);
			} catch (Exception ie) {
			}
			MessageBoxExceptionHandler.process(e);
		}
	}

}
