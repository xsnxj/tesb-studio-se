package org.talend.designer.camel.dependencies.ui.util;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.camel.designer.ui.editor.CamelProcessEditorInput;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;

public class DependenciesUiUtil {
	
	/**
	 * return Editor if the process editor exist
	 * 
	 * @return
	 * @throws PersistenceException
	 * @throws PartInitException
	 */
	public static IEditorPart getCorrespondingProcessEditor(Item item)
			throws PartInitException {
		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		IEditorPart processEditor = null;
		IEditorReference[] editorReferences = activePage.getEditorReferences();
		for (IEditorReference er : editorReferences) {
			if (er == null) {
				continue;
			}
			IEditorInput editorInput = er.getEditorInput();
			if (!(editorInput instanceof CamelProcessEditorInput)) {
				continue;
			}
			CamelProcessEditorInput cEditorInput = (CamelProcessEditorInput) editorInput;
			Item editorItem = cEditorInput.getItem();
			if (editorItem == null || !(editorItem instanceof CamelProcessItem)) {
				continue;
			}
			if (editorItem.getProperty().getId()
					.equals(item.getProperty().getId())
					&& editorItem.getProperty().getVersion()
							.equals(item.getProperty().getVersion())) {
				processEditor = er.getEditor(false);
				break;
			}
		}
		return processEditor;
	}
}
