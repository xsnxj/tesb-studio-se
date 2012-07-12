package org.talend.designer.camel.dependencies.core.ext;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public abstract class AbstractExPredicator<T, K> {

	private Set<ExPredicate> predicates = new HashSet<ExPredicate>();

	void addPredicate(ExPredicate predicate) {
		predicates.add(predicate);
	}

	private boolean satisfy(NodeType n) {
		EList<?> elementParameter = n.getElementParameter();
		for (ExPredicate p : predicates) {
			String attributeName = p.getAttributeName();
			String attributeValue = p.getAttributeValue();
			if (attributeValue == null) {
				continue;
			}
			boolean regex = p.isRegex();
			for (Object o : elementParameter) {
				ElementParameterType ept = (ElementParameterType) o;
				if (!ept.getName().equals(attributeName)) {
					continue;
				}
				String value = ept.getValue();
				if (regex) {
					if (value == null) {
						return false;
					} else {
						if (!Pattern.matches(attributeValue, value.trim())) {
							return false;
						}

					}
				} else if (!attributeValue.equals(value)) {
					return false;
				}
			}
		}
		return true;
	}

	public T toTargets(NodeType t) {
		if (satisfy(t)) {
			return to(t);
		}
		return null;
	}

	protected abstract T to(NodeType t);
	
	/**
	 * only used to Import-Package and Require-Bundle
	 * for Bundle-classpath, it's not supported
	 * all predicates will be ignore
	 * @return
	 */
	public abstract K toTargetIgnorePredicates();
}
