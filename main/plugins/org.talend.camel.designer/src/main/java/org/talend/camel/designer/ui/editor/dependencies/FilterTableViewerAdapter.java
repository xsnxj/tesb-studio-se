package org.talend.camel.designer.ui.editor.dependencies;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.talend.camel.designer.CamelDesignerPlugin;
import org.talend.designer.camel.dependencies.core.model.IDependencyItem;
import org.talend.designer.camel.dependencies.core.model.OsgiDependencies;

public class FilterTableViewerAdapter {

	// hightlight of filterString
	private static Color hightLight = null;
	// builtIn font
	private static Font builtInFont = null;

	private String filterString = null;
	private boolean showBuiltIn = true;

	private final TableViewer tableViewer;

	public FilterTableViewerAdapter(TableViewer tableViewer) {
		this.tableViewer = tableViewer;

		Display display = tableViewer.getTable().getDisplay();
		if (hightLight == null) {
			hightLight = display.getSystemColor(SWT.COLOR_YELLOW);
		}
		if (builtInFont == null) {
			FontData[] fontData = tableViewer.getTable().getFont().getFontData();
			fontData[0].setStyle(fontData[0].getStyle() | SWT.ITALIC);
			builtInFont = new Font(display, fontData[0]);
		}

		tableViewer.setLabelProvider(new DependenciesTableLabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.addFilter(new DependencesTableFilter());
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
		tableViewer.refresh();
		tableViewer.getTable().redraw();
	}

	public void setShowBuiltIn(boolean showBuiltIn) {
		this.showBuiltIn = showBuiltIn;
		tableViewer.refresh();
	}

	public TableViewer getTableViewer() {
        return tableViewer;
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

	private class DependenciesTableLabelProvider extends StyledCellLabelProvider {

	    public Image getImage(Object element) {
			if (element instanceof IDependencyItem) {
				switch (((IDependencyItem) element).getType()) {
				case IDependencyItem.IMPORT_PACKAGE:
					return CamelDesignerPlugin.getImage(((OsgiDependencies) element).isOptional()
					    ? CamelDesignerPlugin.IMPORT_PACKAGE_OVERLAY_ICON
					    : CamelDesignerPlugin.IMPORT_PKG_ICON);
				case IDependencyItem.REQUIRE_BUNDLE:
					return CamelDesignerPlugin.getImage(((OsgiDependencies) element).isOptional()
					    ? CamelDesignerPlugin.REQUIRE_BUNDLE_OVERLAY_ICON
					    : CamelDesignerPlugin.REQUIRE_BD_ICON);
				case IDependencyItem.CLASS_PATH:
					return CamelDesignerPlugin.getImage(CamelDesignerPlugin.BUNDLE_CP_ICON);
				case IDependencyItem.EXPORT_PACKAGE:
					return CamelDesignerPlugin.getImage(CamelDesignerPlugin.IMPORT_PKG_ICON);
				default:
					break;
				}
			}
			return null;
		}

//        @Override
        public Font getFont(Object element) {
            if (element instanceof IDependencyItem && ((IDependencyItem) element).isBuiltIn()) {
                return builtInFont;
            }
            return null;
        }

        @Override
        public void update(ViewerCell cell) {
            IDependencyItem item = (IDependencyItem) cell.getElement();
            final String text = item.getLabel();
            cell.setText(text);
            cell.setImage(getImage(item));
            cell.setFont(getFont(item));
            if (filterString != null && !filterString.isEmpty()) {
                int filterIndex = text.indexOf(filterString);
                StyleRange styleRange = new StyleRange(filterIndex, filterString.length(), null, hightLight);
                cell.setStyleRanges(new StyleRange[] { styleRange });
            } else {
                cell.setStyleRanges(null);
            }
        }
	}

}
