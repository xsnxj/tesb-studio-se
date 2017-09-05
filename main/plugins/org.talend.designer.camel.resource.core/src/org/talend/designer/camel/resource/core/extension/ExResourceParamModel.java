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

import org.talend.core.model.process.INode;

/**
 * @author xpli
 *
 */
public class ExResourceParamModel {

	private String paramName;

	private ExExpressionModel express;

	/**
	 * @return the express
	 */
	public ExExpressionModel getExpress() {
		return express;
	}

	/**
	 * @param express
	 *            the express to set
	 */
	public void setExpress(ExExpressionModel express) {
		this.express = express;
	}

	public boolean eualate(INode node) {
		if (express == null) {
			return true;
		}
		return express.evalute(node);
	}

	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}

	/**
	 * @param paramName
	 *            the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
