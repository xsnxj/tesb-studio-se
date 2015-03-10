package org.talend.designer.camel.dependencies.core.util;

import java.util.Comparator;

import org.talend.designer.camel.dependencies.core.model.IDependencyItem;

/**
 * builtIn items will be sorted automatically and can't be re-sorted
 * Non-BuiltIn items no sort and can be re-sorted
 * @author liugang
 *
 */
public class DependenciesListSorter implements Comparator<IDependencyItem> {

	@Override
	public int compare(IDependencyItem e1, IDependencyItem e2) {
		if (e1 == null || e2 == null) {
			return 0;
		}
		boolean builtIn1 = e1.isBuiltIn();
		boolean builtIn2 = e2.isBuiltIn();
		if (builtIn1 && builtIn2) {
			return e1.getLabel().compareTo(e2.getLabel());
		}
		if (builtIn1 && !builtIn2) {
			return -1;
		} else if (!builtIn1 && builtIn2) {
			return 1;
		}
		return 0;
	}

}