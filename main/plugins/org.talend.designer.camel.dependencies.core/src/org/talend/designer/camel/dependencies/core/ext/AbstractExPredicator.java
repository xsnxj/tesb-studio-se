package org.talend.designer.camel.dependencies.core.ext;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.ElementValueType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;

public abstract class AbstractExPredicator<T> {

    String name;
    boolean isOptional;
	private final Collection<ExPredicate> predicates = new HashSet<ExPredicate>();

    void setName(String name) {
        this.name = name;
    }

    void setOptional(boolean isOptional) {
        this.isOptional = isOptional;
    }

	void addPredicate(ExPredicate predicate) {
		predicates.add(predicate);
	}

	private boolean satisfy(NodeType n) {
	    if (predicates.isEmpty()) {
	        return true;
	    }
		EList<?> parameters = n.getElementParameter();
		String componentName = n.getComponentName();
		for (ExPredicate p : predicates) {
			String attributeName = p.getAttributeName();
			String attributeValue = p.getAttributeValue();
			/*
			 * LOG: if no name or value, the predicate will be ignored
			 */
			if (attributeValue == null || attributeName == null) {
//				System.out.println(componentName + " ignored: " + attributeName
//						+ ", " + attributeValue);
				continue;
			}
			boolean regex = p.isRegex();
			boolean matched = false;
			String[] segments = attributeName.split("\\.");
			/*
			 * a single attribute
			 */
			if (segments.length == 1) {
				matched = searchFromSingle(componentName, attributeName,
						attributeValue, regex, parameters);
			} else if (segments.length == 2) {
				matched = searchFromComplex(componentName, segments,
						attributeValue, regex, parameters);
			}

			/*
			 * LOG: if the attribute didn't find, then ignore it
			 */
			if (!matched) {
//				System.out.println(componentName + ": Attribute "
//						+ attributeName + " doesn't match the expect value "
//						+ attributeValue);
				return false;
			}
		}
		return true;
	}

	private boolean searchFromComplex(String componentName, String[] segments,
			String attributeValue, boolean regex, EList<?> parameters) {
		String parentAttrName = segments[0];
		String childAttrName = segments[1];
		boolean found = false;
		for (Object o : parameters) {
			ElementParameterType ept = (ElementParameterType) o;
			/*
			 * if not parent, then continue
			 */
			if (!ept.getName().equals(parentAttrName)) {
				continue;
			}
			/*
			 * if not a complex value, then continue
			 */
			List<?> elementValues = ept.getElementValue();
			if (elementValues == null) {
				continue;
			}
			for (Object e : elementValues) {
				/*
				 * if not a complex type, then continue
				 */
				if (!(e instanceof ElementValueType)) {
					continue;
				}
				/*
				 * if not child, then continue
				 */
				ElementValueType evt = (ElementValueType) e;
				if (!childAttrName.equals(evt.getElementRef())) {
					continue;
				}
				found = true;
				String value = evt.getValue();
				/**
				 * if didn't find currently, then continue
				 */
				if (regex) {
					if (value == null) {
						continue;
					} else {
						if (!Pattern.matches(attributeValue, value.trim())) {
							continue;
						}

					}
				}
				/*
				 * else compare value
				 */
				else if (!attributeValue.equals(value)) {
					continue;
				}
				//else return true
				return true;
			}
			break;
		}
		/*
		 * LOG: if the attribute didn't find, then ignore it
		 * else return false;
		 */
		if (!found) {
//			System.out.println(componentName + " didn't find: "
//					+ parentAttrName + "." + childAttrName);
			return true;
		}else{
			return false;
		}
	}

	protected boolean searchFromSingle(String componentName,
			String attributeName, String attributeValue, boolean regex,
			EList<?> parameters) {
		boolean found = false;
		for (Object o : parameters) {
			ElementParameterType ept = (ElementParameterType) o;
			if (!ept.getName().equals(attributeName)) {
				continue;
			}
			found = true;
			String value = ept.getValue();
			/**
			 * if it's regex
			 */
			if (regex) {
				if (value == null) {
					return false;
				} else {
					if (!Pattern.matches(attributeValue, value.trim())) {
						return false;
					}else{
						return true;
					}

				}
			}
			/*
			 * else compare value
			 */
			else if (!attributeValue.equals(value)) {
				return false;
			}else{
				return true;
			}
		}
		/*
		 * LOG: if the attribute didn't find, then ignore it
		 * else return false;
		 */
		if (!found) {
//			System.out
//					.println(componentName + " didn't find: " + attributeName);
			return true;
		}else{
			return false;
		}
	}

	public T toTargets(NodeType t) {
		if (satisfy(t)) {
			return to(t);
		}
		return null;
	}

	protected abstract T to(NodeType t);

}
