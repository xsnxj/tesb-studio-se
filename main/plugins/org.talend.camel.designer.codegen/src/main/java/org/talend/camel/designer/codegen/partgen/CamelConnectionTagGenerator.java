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
package org.talend.camel.designer.codegen.partgen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.INode;

/**
 * Use to generate connection prefix or suffix for some special component. For
 * example, component cLoop's loop flow is wrapped in a subProcess, need to
 * append an ".end()" after the whole loop process(before route connecton).
 * Normally, the ".end()" tag will append before the Route
 * connection(ROUTE_ENDBLCOK type). But when no Route conneciton, it will parse
 * wrong. This class is used in cases like this to provide connection
 * prefix/suffix.
 * </br>
 * Extend use: add {@link #TagGenerator} to {@link #generators} map in constructor method.
 * 
 * @author GaoZone
 */
public class CamelConnectionTagGenerator {

	private final static String EMPTY_STRING = "";
	private final static CamelConnectionTagGenerator INSTANCE = new CamelConnectionTagGenerator();

	private abstract class TagGenerator {
		String generateStartTag(INode node, IConnection conn) {
			return EMPTY_STRING;
		}

		String generateEndTag(INode node, IConnection conn) {
			return EMPTY_STRING;
		};
	}

	private Map<String, TagGenerator> generators;

	private CamelConnectionTagGenerator() {
		generators = new HashMap<String, TagGenerator>();

		// Fix [TESB-12471], the only connection is LOOP, then append .end()
		generators.put("cLoop", new TagGenerator() {
			public String generateEndTag(INode node, IConnection conn) {
				if (conn.getConnectorName().equals("LOOP")) {
					List<? extends IConnection> outs = node
							.getOutgoingCamelSortedConnections();
					if (outs.size() == 1) {
						return ".end()";
					}
				}
				return null;
			}
		});
	}

	public static String generateEndTag(INode node, IConnection connection) {
		return INSTANCE.generateTag(node, connection, false);
	}

	public static String generateStartTag(INode node, IConnection connection) {
		return INSTANCE.generateTag(node, connection, true);
	}

	private String generateTag(INode node, IConnection conn, boolean isStartTag) {
		String result = EMPTY_STRING;
		TagGenerator generator = generators.get(node.getComponent().getName());
		if (generator != null) {
			if (isStartTag) {
				result = generator.generateStartTag(node, conn);
			} else {
				result = generator.generateEndTag(node, conn);
			}
		}
		if (result == null) {
			result = EMPTY_STRING;
		}
		return result;
	}
}
