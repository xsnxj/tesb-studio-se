package org.talend.designer.camel.resource.ui.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.talend.camel.core.model.camelProperties.RouteResourceItem;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.designer.camel.resource.editors.ResourceEditorListener;
import org.talend.designer.camel.resource.editors.RouteResourceEditor;
import org.talend.designer.camel.resource.editors.RouteResoureChangeListener;
import org.talend.designer.camel.resource.editors.input.RouteResourceInput;
import org.talend.repository.model.IRepositoryNode;

public class FindPreferEditorUtil {

	private static FindPreferEditorUtil INSTANCE = new FindPreferEditorUtil();

	private FindPreferEditorUtil() {
	}

	/**
	 * find a prefer editor from all existing editors according to the
	 * fileExtensions
	 * 
	 * @param fileExtension
	 * @return
	 */
	private String findPreferEditor(RouteResourceInput fileEditorInput) {
		String editorId = RouteResourceEditor.ID;

		Object underlingFile = fileEditorInput.getAdapter(IFile.class);
		if (underlingFile == null) {
			return editorId;
		}

		IEditorDescriptor ed = null;
		try {
			ed = IDE.getEditorDescriptor((IFile) underlingFile, true);
		} catch (PartInitException e) {
			return editorId;
		}
		if (ed == null) {
			return editorId;
		}
		String id = ed.getId();
		if (id == null || id.trim() == null) {
			return editorId;
		}
		return id;
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
		RouteResourceInput fileEditorInput = RouteResourceInput
				.createInput(item);
		fileEditorInput.setRepositoryNode(node);

		openEditor(page, fileEditorInput, item, RouteResourceEditor.ID);
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
		RouteResourceInput fileEditorInput = RouteResourceInput
				.createInput(item);
		fileEditorInput.setRepositoryNode(node);

		openEditor(page, fileEditorInput, item,
				FindPreferEditorUtil.INSTANCE.findPreferEditor(fileEditorInput));
	}

	/**
	 * Open or bind Route resource editor by specifing editorID
	 * 
	 * @param page
	 * @param node
	 * @param item
	 */
	public static void openEditor(final IWorkbenchPage page,
			RouteResourceInput fileEditorInput, RouteResourceItem item,
			String editorId) {
		try {

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
