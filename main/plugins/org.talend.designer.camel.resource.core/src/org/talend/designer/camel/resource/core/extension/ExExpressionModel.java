// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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
public class ExExpressionModel {

	public static final String AND = "and";
	public static final String OR = "or";
	public static final String NOT = "not";

	private ExExpressionModel leftModel;

	private ExExpressionModel rightModel;

	private String operator;

	public boolean evalute(INode node) {
		if (AND.equals(operator)) {
			return (leftModel != null && leftModel.evalute(node))
					&& (rightModel == null || rightModel.evalute(node));
		} else if (OR.equals(operator)) {
			if (leftModel != null && rightModel != null) {
				return leftModel.evalute(node) || rightModel.evalute(node);
			} else {
				return false;
			}
		} else if (NOT.equals(operator)) {
			if (leftModel != null) {
				return !leftModel.evalute(node);
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * @return the leftModel
	 */
	public ExExpressionModel getLeftModel() {
		return leftModel;
	}

	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * @return the rightModel
	 */
	public ExExpressionModel getRightModel() {
		return rightModel;
	}


	/**
	 * @param leftModel
	 *            the leftModel to set
	 */
	public void setLeftModel(ExExpressionModel leftModel) {
		this.leftModel = leftModel;
	}

	/**
	 * @param operator
	 *            the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 * @param rightModel
	 *            the rightModel to set
	 */
	public void setRightModel(ExExpressionModel rightModel) {
		this.rightModel = rightModel;
	}
}
