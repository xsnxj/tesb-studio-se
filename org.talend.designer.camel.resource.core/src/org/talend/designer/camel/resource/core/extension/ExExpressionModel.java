// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

	private String opperator;

	public boolean evalute(INode node) {
		if (AND.equals(opperator)) {
			if (leftModel != null && rightModel != null) {
				return leftModel.evalute(node) && rightModel.evalute(node);
			} else {
				return false;
			}
		} else if (OR.equals(opperator)) {
			if (leftModel != null && rightModel != null) {
				return leftModel.evalute(node) || rightModel.evalute(node);
			} else {
				return false;
			}
		} else if (NOT.equals(opperator)) {
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
	 * @return the opperator
	 */
	public String getOpperator() {
		return opperator;
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
	 * @param opperator
	 *            the opperator to set
	 */
	public void setOpperator(String opperator) {
		this.opperator = opperator;
	}

	/**
	 * @param rightModel
	 *            the rightModel to set
	 */
	public void setRightModel(ExExpressionModel rightModel) {
		this.rightModel = rightModel;
	}
}
