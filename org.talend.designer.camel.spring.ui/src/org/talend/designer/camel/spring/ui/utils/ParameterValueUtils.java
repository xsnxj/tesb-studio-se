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
package org.talend.designer.camel.spring.ui.utils;

/**
 * DOC LiXP class global comment. Detailled comment
 */
public final class ParameterValueUtils {

    /**
     * 
     * Ensure that the string is surrounded by quotes.
     * 
     * @param string
     * @return
     */
    public static String quotes(String string) {
        if (string == null) {
            return "\"\"";
        }
        String result = string;
        if (!result.startsWith("\"")) {
            result = "\"" + result;
        }

        if (!result.endsWith("\"")) {
            result = result + "\"";
        }
        return result;
    }

    /**
     * 
     * Ensure that the string is not surrounded by quotes.
     * 
     * @param string
     * @return
     */
    public static String unquotes(String string) {
        if (string == null) {
            return "";
        }
        String result = string;
        if (result.startsWith("\"")) {
            result = result.substring(1);
        }

        if (result.endsWith("\"")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
