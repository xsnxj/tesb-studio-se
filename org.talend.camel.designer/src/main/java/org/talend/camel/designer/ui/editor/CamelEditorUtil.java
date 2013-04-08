package org.talend.camel.designer.ui.editor;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.talend.repository.model.RepositoryNode;

public class CamelEditorUtil {

	/**
	 * check if there are more than one editors
	 * coming from the same RepositoryNode
	 * @param node
	 * @return
	 */
	public static boolean hasMultiEditorOpened(RepositoryNode node) {
		if (node == null) {
			return false;
		}
		try {
			int count = 0;
			IEditorReference[] editorReferences = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
			for (IEditorReference r : editorReferences) {
				try {
					Object adapter = r.getEditorInput().getAdapter(
							RepositoryNode.class);
					if (adapter != null && node == adapter) {
						count++;
					}
					if (count > 1) {
						return true;
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * check if there's any editor is opened which
	 * coming from the RepositoryNode
	 * @param node
	 * @return
	 */
	public static boolean hasEditorOpened(RepositoryNode node) {
		if (node == null) {
			return false;
		}
		try {
			IEditorReference[] editorReferences = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
			for (IEditorReference r : editorReferences) {
				try {
					Object adapter = r.getEditorInput().getAdapter(
							RepositoryNode.class);
					if(adapter == null || !(adapter instanceof RepositoryNode)){
						continue;
					}
					
					if (adapter != null
							&& node.equals(adapter)
							&& node.getObject()
									.getVersion()
									.equals(((RepositoryNode) adapter)
											.getObject().getVersion())) {
						return true;
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * check if there's any another editor coming from the RepositoryNode
	 * coming from the same RepositoryNode
	 * @param node: {@link RepositoryNode}
	 * @param input: except this input
	 * @return
	 */
	public static boolean hasMoreEditorOpenedExcept(RepositoryNode node,
			IEditorInput input) {
		if (node == null) {
			return false;
		}
		try {
			IEditorReference[] editorReferences = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();
			for (IEditorReference r : editorReferences) {
				try {
					IEditorInput editorInput = r.getEditorInput();
					if (editorInput == input) {
						continue;
					}
					Object adapter = editorInput
							.getAdapter(RepositoryNode.class);
					if (node == adapter) {
						return true;
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
