package org.talend.camel.designer.ui.view;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

public class SpringConfigurationView extends PageBookView {

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage("No Spring available");
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get an outline page.
		Object obj = getAdapter(part, ISpringConfigurationPage.class, false);
		if (obj instanceof ISpringConfigurationPage) {
			ISpringConfigurationPage page = (ISpringConfigurationPage) obj;
			if (page instanceof IPageBookViewPage) {
				initPage((IPageBookViewPage) page);
			}
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no content outline
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		ISpringConfigurationPage page = (ISpringConfigurationPage) rec.page;
		page.dispose();
		rec.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			return page.getActiveEditor();
		}

		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		// We only care about editors
		return (part instanceof IEditorPart);
	}

	public static Object getAdapter(Object sourceObject, Class adapter,
			boolean activatePlugins) {
		Assert.isNotNull(adapter);
		if (sourceObject == null) {
			return null;
		}
		if (adapter.isInstance(sourceObject)) {
			return sourceObject;
		}

		if (sourceObject instanceof IAdaptable) {
			IAdaptable adaptable = (IAdaptable) sourceObject;

			Object result = adaptable.getAdapter(adapter);
			if (result != null) {
				// Sanity-check
				Assert.isTrue(adapter.isInstance(result));
				return result;
			}
		}

		if (!(sourceObject instanceof PlatformObject)) {
			Object result;
			if (activatePlugins) {
				result = Platform.getAdapterManager().loadAdapter(sourceObject,
						adapter.getName());
			} else {
				result = Platform.getAdapterManager().getAdapter(sourceObject,
						adapter);
			}
			if (result != null) {
				return result;
			}
		}

		return null;
	}

}
