package org.talend.designer.camel.dependencies.ui.editor;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.ui.UIActivator;

public class RouterDependenciesTableViewer extends TableViewer implements
		Listener {

	// selected foreground
	private static Color selectedFc = null;
	// default builtIn foreground
	private static Color builtInFc = null;
	// hightlight of filterString
	private static Color hightLight = null;
	// builtIn font
	private static Font builtInFont = null;

	private String filterString = null;
	private boolean showBuiltIn = true;

	public RouterDependenciesTableViewer(Table table) {
		super(table);

		Display display = getTable().getDisplay();
		if (builtInFc == null) {
			hightLight = display.getSystemColor(SWT.COLOR_YELLOW);
			selectedFc = display.getSystemColor(SWT.COLOR_WHITE);
			builtInFc = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		}
		if (builtInFont == null) {
			FontData[] fontData = getTable().getFont().getFontData();
			fontData[0].setStyle(fontData[0].getStyle() | SWT.ITALIC);
			builtInFont = new Font(display, fontData[0]);
		}

		setLabelProvider(new DependenciesTableLabelProvider());
		setContentProvider(new ArrayContentProvider());
//		setSorter(new DependenciesTableSorter());
		addFilter(new DependencesTableFilter());

		getTable().addListener(SWT.PaintItem, this);
		getTable().addListener(SWT.EraseItem, this);
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.EraseItem:
			event.detail &= ~SWT.FOREGROUND;
			break;
		case SWT.PaintItem:
			paintTableItem(event);
		default:
			break;
		}
	}

	private void paintTableItem(Event event) {
		GC gc = event.gc;
		TableItem ti = (TableItem) event.item;
		Color foreground = gc.getForeground();
		Color background = gc.getBackground();
		Font font = gc.getFont();
		
		int x = event.x;
		int y = event.y;

		// set font and foreground for builtIn item
		Object data = ti.getData();
		if (data != null && data instanceof IDependencyItem
				&& ((IDependencyItem) data).isBuiltIn()) {
			TableItem[] selection = getTable().getSelection();
			boolean isSelected = false;
			for (TableItem i : selection) {
				if (ti == i) {
					isSelected = true;
					break;
				}
			}
			if (isSelected) {
				gc.setForeground(selectedFc);
			} else {
				gc.setForeground(builtInFc);
			}
			gc.setFont(builtInFont);
		}

		// draw image
		Image image = ti.getImage();
		if (image != null) {
			int width = image.getBounds().width;
			gc.drawImage(image, x, y);
			x += width + 2;
		}

		String text = ti.getText();
		//highlight filterString
		if (filterString != null && !"".equals(filterString)) { //$NON-NLS-1$
			int filterIndex = text.indexOf(filterString);
			if (filterIndex != -1) {
				String substring = text.substring(0, filterIndex);
				Point preTextExtent = gc.textExtent(substring);
				Point textExtent = gc.textExtent(filterString);
				gc.setBackground(hightLight);
				gc.fillRectangle(new Rectangle(x + preTextExtent.x, y,
						textExtent.x, event.height));
				gc.setBackground(background);
			}
		}
		gc.drawText(text, x, y + 1, true);

		// reset
		gc.setForeground(foreground);
		gc.setBackground(background);
		gc.setFont(font);
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
		refresh();
		getTable().redraw();
	}

	public void setShowBuiltIn(boolean showBuiltIn) {
		this.showBuiltIn = showBuiltIn;
		refresh();
	}

	/**
	 * filter according to the filterString and show BuiltIn items or not
	 * @author liugang
	 *
	 */
	private class DependencesTableFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (!showBuiltIn) {
				if (element instanceof IDependencyItem
						&& ((IDependencyItem) element).isBuiltIn()) {
					return false;
				}
			}
			if (filterString == null || filterString.equals("")) { //$NON-NLS-1$
				return true;
			}
			if (element instanceof IDependencyItem) {
				String label = ((IDependencyItem) element).getLabel();
				if (label.contains(filterString)) {
					return true;
				}
			}
			return false;
		}

	}

	private class DependenciesTableLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			if (element instanceof IDependencyItem) {
				return ((IDependencyItem) element).getLabel();
			}
			return super.getText(element);
		}

		@Override
		public Image getImage(Object element) {
			if (element instanceof IDependencyItem) {
				switch (((IDependencyItem) element).getType()) {
				case IDependencyItem.IMPORT_PACKAGE:
					return UIActivator.getImage(UIActivator.IMPORT_PKG_ICON);
				case IDependencyItem.REQUIRE_BUNDLE:
					return UIActivator.getImage(UIActivator.REQUIRE_BD_ICON);
				case IDependencyItem.CLASS_PATH:
					return UIActivator.getImage(UIActivator.BUNDLE_CP_ICON);
				default:
					break;
				}
			}
			return super.getImage(element);
		}

	}

//	/**
//	 * builtIn items will be sorted automatically and can't be re-sorted
//	 * Non-BuiltIn items no sort and can be re-sorted
//	 * @author liugang
//	 *
//	 */
//	private class DependenciesTableSorter extends ViewerSorter {
//
//		@Override
//		public int category(Object element) {
//			if (element instanceof IDependencyItem) {
//				if (((IDependencyItem) element).isBuiltIn()) {
//					return -1;
//				}
//				return 0;
//			}
//			return super.category(element);
//		}
//
//		@Override
//		public int compare(Viewer viewer, Object e1, Object e2) {
//			if (e1 != null && e2 != null && e1 instanceof IDependencyItem
//					&& e2 instanceof IDependencyItem) {
//				boolean builtIn1 = ((IDependencyItem) e1).isBuiltIn();
//				boolean builtIn2 = ((IDependencyItem) e2).isBuiltIn();
//				if (builtIn1 && builtIn2) {
//					return ((IDependencyItem) e1).getLabel().compareTo(
//							((IDependencyItem) e2).getLabel());
//				}
//				if (builtIn1 && !builtIn2) {
//					return -1;
//				} else if (!builtIn1 && builtIn2) {
//					return 1;
//				}
//			}
//			return 0;
//		}
//
//	}
}
