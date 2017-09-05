// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.designer.camel.resource.core.extension;

import org.talend.core.model.process.IElementParameter;
import org.talend.core.model.process.INode;

/**
 * @author xpli
 *
 */
public class ExAttributeExpressionModel extends ExExpressionModel {

	private String name;

	private String value;

	private boolean isRegex;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the isRegex
	 */
	public boolean isRegex() {
		return isRegex;
	}

	/**
	 * @param isRegex
	 *            the isRegex to set
	 */
	public void setRegex(boolean isRegex) {
		this.isRegex = isRegex;
	}

	@Override
	public boolean evalute(INode node) {

		if (name == null && value == null) {
			return false;
		}

		IElementParameter parameter = node.getElementParameter(name);
		if (parameter == null) {
			return false;
		}
		Object valueObj = parameter.getValue();
		if (valueObj == null) {
			return false;
		}
		if (isRegex) {
			return ((String) valueObj).matches(value);
		} else {
			return valueObj.equals(value);
		}
	}
}
