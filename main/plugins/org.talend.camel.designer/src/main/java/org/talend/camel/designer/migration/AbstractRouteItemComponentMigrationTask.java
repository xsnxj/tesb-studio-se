// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.camel.designer.migration;

import java.util.List;

import org.talend.camel.core.model.camelProperties.CamelProcessItem;
import org.talend.commons.exception.ExceptionHandler;
import org.talend.designer.core.model.utils.emf.talendfile.ElementParameterType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.TalendFileFactory;


/**
 * Migration Task only handle nodes of one specific camel component.
 */
public abstract class AbstractRouteItemComponentMigrationTask extends
		AbstractRouteItemMigrationTask {

	/**
	 * UtilTool to handle NodeType.
	 */
	protected static class UtilTool{
		public static ElementParameterType findParameterType(NodeType node, String paramName) {
			List<?> params = node.getElementParameter();
			for (Object param : params) {
				ElementParameterType paramType = (ElementParameterType) param;
				if(paramType.getName().equals(paramName)) {
					return paramType;
				}
			}
			return null;
		}

		public static String getParameterValue(NodeType currentNode, String paramName) {
			ElementParameterType param = findParameterType(currentNode,paramName);
			if(param == null) {
				return null;
			}
			return param.getValue();
		}

		/**
		 * Replace param sub sequence.
		 *
		 * @return true, if rename value sub string
		 */
		public static boolean replaceValueSubSequence(ElementParameterType param, CharSequence oldSeq, CharSequence newSeq) {
			String oldValue = param.getValue();
			if(!oldValue.contains(oldSeq)) {
				return false;
			}
			String newValue = oldValue.replace(oldSeq, newSeq);
			param.setValue(newValue);
			return true;
		}

		@SuppressWarnings("unchecked")
		public static boolean addParameterType(NodeType node, ElementParameterType param) {
			return node.getElementParameter().add(param);
		}

		public static boolean removeParameterType(NodeType node, ElementParameterType param) {
			return node.getElementParameter().remove(param);
		}

		public static ElementParameterType createParameterType(String field, String name, String value) {
	        return createParameterType(field, name, value, null);
	    }

		@SuppressWarnings("unchecked")
		public static ElementParameterType createParameterType(String field, String name, String value, List<?> elementParameterTypes) {
	        ElementParameterType paramType = TalendFileFactory.eINSTANCE.createElementParameterType();
	        paramType.setField(field);
	        paramType.setName(name);
	        paramType.setValue(value);
	        if (elementParameterTypes != null) {
	            paramType.getElementValue().addAll(elementParameterTypes);
	        }
	        return paramType;
	    }

		public static void addParameterType(NodeType node, String field, String name, String value, List<?> elementParameterTypes) {
			ElementParameterType paramType = createParameterType(field, name, value, elementParameterTypes);
			addParameterType(node, paramType);
		}

		public static void addParameterType(NodeType node, String field, String name, String value) {
			addParameterType(node, field, name, value, null);
		}
	}

	/**
	 * the regex patten to filter component name.
	 * 
	 * @return the component name regex
	 */
	public abstract String getComponentNameRegex();

	/* 
	 * 
	 */
	/* (non-Javadoc)
	 * @see org.talend.camel.designer.migration.AbstractRouteItemMigrationTask#execute(org.talend.camel.core.model.camelProperties.CamelProcessItem)
	 */
	@Override
	public final ExecutionResult execute(CamelProcessItem item) {
		List<NodeType> nodes = findComponentNodesRegex(item, getComponentNameRegex());
		if (nodes.isEmpty()) {
			return ExecutionResult.NOTHING_TO_DO;
		}
		try {
			boolean needSave = false;
			for (NodeType node : nodes) {
				needSave |= execute(node);
			}
			if (needSave) {
				saveItem(item);
				return ExecutionResult.SUCCESS_NO_ALERT;
			} else {
				return ExecutionResult.NOTHING_TO_DO;
			}
		} catch (Exception e) {
			ExceptionHandler.process(e);
			return ExecutionResult.FAILURE;
		}
	}

	/**
	 * execute migration tasks by handle node, return indicator: whether need
	 * save.
	 *
	 * @param node the Component node, can't not be null.
	 * @return whether need save items. true: item need save, false: item not
	 * modified.
	 * @throws Exception the exception
	 */
	protected abstract boolean execute(NodeType node) throws Exception;
}
