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
package org.talend.designer.camel.spring.ui.exports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.talend.core.model.components.ComponentUtilities;
import org.talend.core.model.process.EConnectionType;
import org.talend.designer.camel.spring.core.ICamelSpringConstants;
import org.talend.designer.camel.spring.core.models.SpringRoute;
import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.talend.designer.camel.spring.ui.RouteMapping;
import org.talend.designer.core.model.utils.emf.talendfile.ConnectionType;
import org.talend.designer.core.model.utils.emf.talendfile.NodeType;
import org.talend.designer.core.model.utils.emf.talendfile.ProcessType;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public class SpringXMLExporter {

	private static final int TMP_TRY = ICamelSpringConstants.TMP_TRY;

	private List<SpringRoute> springRoutes;

	private Map<String, SpringRouteNode> routeNodes;

	private static SpringXMLExporter instance = new SpringXMLExporter();

	/**
	 * 
	 * DOC LiXP Comment method "getInstance".
	 * 
	 * @return
	 */
	public static SpringXMLExporter getInstance() {
		return instance;
	}

	private Map<String, IExportParameterHandler> handlers;

	private SpringXMLExporter() {
		this.handlers = ExportParameterProviderFactory.ISNTANCE
				.getExParameterHandlers();
		this.routeNodes = new HashMap<String, SpringRouteNode>();
		this.springRoutes = new ArrayList<SpringRoute>();
	}

	public SpringRoute[] buildSpringRoute(ProcessType process) {
		// Clear cache.
		springRoutes.clear();
		routeNodes.clear();

		Map<String, NodeType> uniqueNodes = cacheNodeTypee(process);

		for (Object obj : process.getConnection()) {
			ConnectionType connection = (ConnectionType) obj;
			String source = connection.getSource();
			String target = connection.getTarget();
			NodeType sourceNode = uniqueNodes.get(source);
			NodeType targeteNode = uniqueNodes.get(target);
			if (sourceNode == null || targeteNode == null) { // Ignore
				continue;
			}
			SpringRouteNode routeSourceNode = createSpringNode(sourceNode);
			SpringRouteNode routeTargetNode = createSpringNode(targeteNode);
			routeNodes.put(source, routeSourceNode);
			routeNodes.put(target, routeTargetNode);

			int lineStyle = connection.getLineStyle();
			int connectionRouteType = getConnectionRouteType(lineStyle);
			switch (connectionRouteType) {
			case ICamelSpringConstants.TMP_TRY:
				// Do try connection
				doTryConnection(connection, routeSourceNode, routeTargetNode);
				break;
			case ICamelSpringConstants.WHEN:
				doWhenConnection(connection, routeSourceNode, routeTargetNode);
				break;
			case ICamelSpringConstants.OTHER:
				doOtherConnection(connection, routeSourceNode, routeTargetNode);
				break;
			case ICamelSpringConstants.CATCH:
				doCatchConnection(connection, routeSourceNode, routeTargetNode);
				break;
			case ICamelSpringConstants.FINALLY:
				doFinallyConnection(connection, routeSourceNode,
						routeTargetNode);
				break;
			case ICamelSpringConstants.ROUTE:
				doRouteConnection(routeSourceNode, routeTargetNode);
				break;
			case ICamelSpringConstants.ROUTE_ENDBLOCK:
				doRouteEndConnection(routeSourceNode, routeTargetNode);
				break;
			default:
				//
			}
		}

		// Build the routings.
		for (SpringRouteNode routeNode : routeNodes.values()) {
			if (routeNode.getParent() == null) {// It's a from route node
				SpringRoute route = new SpringRoute();
				route.setRouteId(springRoutes.size());
				route.setFrom(routeNode);
				springRoutes.add(route);
			}
		}

		removeUnnecessaryNode();

		return springRoutes.toArray(new SpringRoute[0]);
	}

	private void doOtherConnection(ConnectionType connection,
			SpringRouteNode routeSourceNode, SpringRouteNode routeTargetNode) {
		int sourceType = routeSourceNode.getType();
		if (sourceType != ICamelSpringConstants.MSGROUTER) {
			throw new IllegalStateException(
					"ROUTE_OTHER doesn't come from cMessageRoute but "
							+ routeSourceNode.getUniqueName());
		}
		SpringRouteNode routeOtherConnectionNode = createSpringNode(connection);
		SpringRouteNode choiceFisrtChild = routeSourceNode.getFirstChild();
		if (choiceFisrtChild == null) {// this is the right situation
			routeSourceNode.setFirstChild(routeOtherConnectionNode);
		} else {
			SpringRouteNode nullSibling = getNullSibling(choiceFisrtChild);
			doSibling(nullSibling, routeOtherConnectionNode);
		}
		// Set the target to the child of route_try
		routeOtherConnectionNode.setFirstChild(routeTargetNode);
		routeNodes.put(routeOtherConnectionNode.getUniqueName(),
				routeOtherConnectionNode);
	}

	private void doCatchConnection(ConnectionType connection,
			SpringRouteNode routeSourceNode, SpringRouteNode routeTargetNode) {
		int sourceType = routeSourceNode.getType();
		if (sourceType != ICamelSpringConstants.TRY) {
			throw new IllegalStateException(
					"ROUTE_CATCH doesn't come from cMessageRoute but "
							+ routeSourceNode.getUniqueName());
		}
		SpringRouteNode routeCatchConnectionNode = createSpringNode(connection);
		SpringRouteNode choiceFisrtChild = routeSourceNode.getFirstChild();
		if (choiceFisrtChild == null) {
			routeSourceNode.setFirstChild(routeCatchConnectionNode);
		} else {
			SpringRouteNode nullSibling = getNullSibling(choiceFisrtChild);
			if (nullSibling.getType() == ICamelSpringConstants.TMP_TRY) {
				doSibling(nullSibling, routeCatchConnectionNode);
			} else if (nullSibling.getType() == ICamelSpringConstants.FINALLY) {
				doSibling(nullSibling.getParent(), routeCatchConnectionNode);
				doSibling(routeCatchConnectionNode, nullSibling);
			} else if (nullSibling.getType() == ICamelSpringConstants.CATCH) {
				doSibling(nullSibling, routeCatchConnectionNode);
			}
		}
		// Set the target to the child of route_try
		routeCatchConnectionNode.setFirstChild(routeTargetNode);
		routeNodes.put(routeCatchConnectionNode.getUniqueName(),
				routeCatchConnectionNode);

	}

	private void doWhenConnection(ConnectionType connection,
			SpringRouteNode routeSourceNode, SpringRouteNode routeTargetNode) {
		int sourceType = routeSourceNode.getType();
		if (sourceType != ICamelSpringConstants.MSGROUTER
				&& sourceType != ICamelSpringConstants.INTERCEPT) {
			throw new IllegalStateException(
					"ROUTE_WHEN doesn't come from cMessageRoute but "
							+ routeSourceNode.getUniqueName());
		}
		SpringRouteNode routeWhenConnectionNode = createSpringNode(connection);
		SpringRouteNode choiceFisrtChild = routeSourceNode.getFirstChild();
		if (choiceFisrtChild == null) {// this is the right situation
			routeSourceNode.setFirstChild(routeWhenConnectionNode);
		} else {
			SpringRouteNode nullSibling = getNullSibling(choiceFisrtChild);
			if (nullSibling.getType() == ICamelSpringConstants.WHEN) {
				doSibling(nullSibling, routeWhenConnectionNode);
			} else if (nullSibling.getType() == ICamelSpringConstants.OTHER) {
				doSibling(nullSibling.getParent(), routeWhenConnectionNode);
				doSibling(routeWhenConnectionNode, nullSibling);
			} else {
				// no way, ignore
			}
		}
		// Set the target to the child of route_try
		routeWhenConnectionNode.setFirstChild(routeTargetNode);
		routeNodes.put(routeWhenConnectionNode.getUniqueName(),
				routeWhenConnectionNode);

	}

	private void doRouteConnection(SpringRouteNode routeSourceNode,
			SpringRouteNode routeTargetNode) {
		int sourceType = routeSourceNode.getType();
		if (isOnExceptionOrIntercept(sourceType)) {
			doChild(routeSourceNode, routeTargetNode);
			return;
		}
		if (isComplicated(sourceType)) {
			doChild(routeSourceNode, routeTargetNode);
			return;
		}
		doSibling(routeSourceNode, routeTargetNode);
	}

	private void doRouteEndConnection(SpringRouteNode routeSourceNode,
			SpringRouteNode routeTargetNode) {
		// Simply set target to be a next sibling of source
		doSibling(routeSourceNode, routeTargetNode);
	}

	private void doFinallyConnection(ConnectionType connection,
			SpringRouteNode routeSourceNode, SpringRouteNode routeTargetNode) {
		SpringRouteNode routeTryConnectionNode = createSpringNode(connection);
		doChild(routeSourceNode, routeTryConnectionNode);
		doChild(routeTryConnectionNode, routeTargetNode);
	}

	/**
	 * 
	 * DOC LiXP Comment method "doTryConnection".
	 * 
	 * @param connection
	 * @param routeSourceNode
	 * @param routeTargetNode
	 */
	private void doTryConnection(ConnectionType connection,
			SpringRouteNode routeSourceNode, SpringRouteNode routeTargetNode) {
		int sourceType = routeSourceNode.getType();
		if (sourceType != ICamelSpringConstants.TRY) {
			throw new IllegalStateException(
					"ROUTE_TRY doesn't come from cTry but "
							+ routeSourceNode.getUniqueName());
		}
		SpringRouteNode routeTryConnectionNode = createSpringNode(connection);
		SpringRouteNode tryFisrtChild = routeSourceNode.getFirstChild();
		if (tryFisrtChild != null) {// it seems someone took off route_try's
									// position, kick off him/her
			routeSourceNode.setFirstChild(routeTryConnectionNode);
			routeTryConnectionNode.setSibling(tryFisrtChild);
		} else {// this is the right situation
			routeSourceNode.setFirstChild(routeTryConnectionNode);
		}
		// Set the target to the child of route_try
		routeTryConnectionNode.setFirstChild(routeTargetNode);
		routeNodes.put(routeTryConnectionNode.getUniqueName(),
				routeTryConnectionNode);
	}

	/**
	 * 
	 * DOC LiXP Comment method "cacheNodeTypee".
	 * 
	 * @param process
	 * @return
	 */
	private Map<String, NodeType> cacheNodeTypee(ProcessType process) {
		Map<String, NodeType> uniqueNodes = new HashMap<String, NodeType>();
		for (Object obj : process.getNode()) {
			NodeType node = (NodeType) obj;
			String uniqueName = ComponentUtilities.getNodeUniqueName(node);
			uniqueNodes.put(uniqueName, node);
		}
		return uniqueNodes;
	}

	/**
	 * 
	 * DOC LiXP Comment method "removeUnnecessaryNode".
	 */
	private void removeUnnecessaryNode() {
		for (SpringRouteNode node : routeNodes.values()) {
			if (node.getType() == TMP_TRY) {
				SpringRouteNode parent = node.getParent();
				parent.setFirstChild(node.getFirstChild());
				SpringRouteNode nullSiblingNode = getNullSibling(node
						.getFirstChild());
				nullSiblingNode.setSibling(node.getSibling());
			}
		}
	}

	/**
	 * Create a {@code SpringRouteNode} according to a {@code ConnectionType}
	 * 
	 * @param connection
	 * @return
	 */
	private SpringRouteNode createSpringNode(ConnectionType connection) {
		String uniqueName = connection.getConnectorName() + "_"
				+ connection.getSource() + "_" + connection.getTarget();
		SpringRouteNode routeNode = new SpringRouteNode(uniqueName);
		int componentType = getConnectionRouteType(connection.getLineStyle());
		routeNode.setType(componentType);
		loadParameters(connection, routeNode);
		return routeNode;
	}

	/**
	 * Create a {@code SpringRouteNode} according to a {@code NodeType}
	 * 
	 * @param nodeType
	 * @return
	 */
	private SpringRouteNode createSpringNode(NodeType nodeType) {
		String uniqueName = ComponentUtilities.getNodeUniqueName(nodeType);
		SpringRouteNode routeNode = routeNodes.get(uniqueName);
		if (routeNode != null) {
			return routeNode;
		}
		routeNode = new SpringRouteNode(uniqueName);
		int componentType = getComponentType(nodeType.getComponentName());
		routeNode.setType(componentType);
		loadParameters(nodeType, routeNode.getParameter());
		return routeNode;
	}

	/**
	 * 
	 * DOC LiXP Comment method "doChild".
	 * 
	 * @param routeSourceNode
	 * @param routeTargetNode
	 */
	private void doChild(SpringRouteNode routeSourceNode,
			SpringRouteNode routeTargetNode) {
		SpringRouteNode child = routeSourceNode.getFirstChild();
		if (child != null) {// it seems someone has taken the first place, bad
							// to be a next sibling of his/her end
							// sibling
			SpringRouteNode nullSiblingChild = getNullSibling(child);
			nullSiblingChild.setSibling(routeTargetNode);
		} else {// this is the best situation, take the first place.
			routeSourceNode.setFirstChild(routeTargetNode);
		}

	}

	/**
	 * 
	 * Simply set target to be a next sibling of source
	 * 
	 * @param routeSourceNode
	 * @param routeTargetNode
	 */
	private void doSibling(SpringRouteNode routeSourceNode,
			SpringRouteNode routeTargetNode) {
		// Simply set target to be a next sibling of source
		routeSourceNode.setSibling(routeTargetNode);
	}

	private int getComponentType(String componentName) {
		for (int index = 0; index < RouteMapping.COMPOMENT_NAMES.length; index++) {
			String component = RouteMapping.COMPOMENT_NAMES[index];
			if (component.equals(componentName)) {
				return index;
			}
		}

		return ICamelSpringConstants.MSGENDPOINT;
	}

	private int getConnectionRouteType(int lineStyle) {
		if (lineStyle == EConnectionType.ROUTE_TRY.getId()) {
			return TMP_TRY;
		} else if (lineStyle == EConnectionType.ROUTE_CATCH.getId()) {
			return ICamelSpringConstants.CATCH;
		} else if (lineStyle == EConnectionType.ROUTE_FINALLY.getId()) {
			return ICamelSpringConstants.FINALLY;
		} else if (lineStyle == EConnectionType.ROUTE_WHEN.getId()) {
			return ICamelSpringConstants.WHEN;
		} else if (lineStyle == EConnectionType.ROUTE_OTHER.getId()) {
			return ICamelSpringConstants.OTHER;
		} else if (lineStyle == EConnectionType.ROUTE_ENDBLOCK.getId()) {
			return ICamelSpringConstants.ROUTE_ENDBLOCK;
		}
		return ICamelSpringConstants.ROUTE;
	}

	/**
	 * 
	 * @param componentType
	 * @return
	 */
	private boolean isComplicated(int componentType) {
		return componentType == ICamelSpringConstants.TRY
				|| componentType == ICamelSpringConstants.AGGREGATE
				|| componentType == ICamelSpringConstants.BALANCE
				|| componentType == ICamelSpringConstants.MSGROUTER
				|| componentType == ICamelSpringConstants.LOOP
				|| componentType == ICamelSpringConstants.IDEM
				|| componentType == ICamelSpringConstants.FILTER
				|| componentType == ICamelSpringConstants.SPLIT
				|| componentType == ICamelSpringConstants.THROTTLER;
	}

	private boolean isOnExceptionOrIntercept(int sourceType) {
		return sourceType == ICamelSpringConstants.EXCEPTION
				|| sourceType == ICamelSpringConstants.INTERCEPT;
	}

	private SpringRouteNode getNullSibling(SpringRouteNode node) {
		SpringRouteNode sibling = node.getSibling();
		if (sibling == null) {
			return node;
		}
		return getNullSibling(sibling);
	}

	private void loadParameters(ConnectionType connection,
			SpringRouteNode routeNode) {
		EList<?> list = connection.getElementParameter();
		String component = getConnectionComponentName(routeNode.getType());
		loadParameters(component, list, routeNode.getParameter());
	}

	private String getConnectionComponentName(int type) {
		String component = "";
		if (type != TMP_TRY) {
			component = RouteMapping.COMPOMENT_NAMES[type];
		}
		return component;
	}

	private void loadParameters(NodeType nodeType, Map<String, String> parameter) {
		EList<?> list = nodeType.getElementParameter();
		String component = nodeType.getComponentName();
		loadParameters(component, list, parameter);
	}

	private void loadParameters(String component,
			EList<?> elementParameterTypes, Map<String, String> parameters) {
		IExportParameterHandler handler = handlers.get(component);
		if (handler == null) {
			// Not support.
			return;
		}
		handler.handleParameters(elementParameterTypes, parameters);
	}
}
