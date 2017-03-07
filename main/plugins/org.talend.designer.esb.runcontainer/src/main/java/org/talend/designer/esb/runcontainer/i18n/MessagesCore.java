// ============================================================================
//
// Talend Community Edition
//
// Copyright (C) 2006-2013 Talend – www.talend.com
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
//
// ============================================================================
package org.talend.designer.esb.runcontainer.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * Core of i18n management.<br/>
 * 
 * Features :
 * <ul>
 * <li>String without args</li>
 * <li>String with args</li>
 * </ul>
 * Coming features :
 * <ul>
 * <li>Dates</li>
 * <li>Using many file per plug-in</li>
 * </ul>
 * 
 * Using plug-in can create their implementation by copy the DefaultMessagesImpl in the same package.<br/>
 * * $Id: MessagesCore.java,v 1.8 2006/07/26 16:02:00 amaumont Exp $
 * 
 */
public abstract class MessagesCore {

    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$

    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    private static Logger log = Logger.getLogger(MessagesCore.class);

    public static final String KEY_NOT_FOUND_PREFIX = "!!!"; //$NON-NLS-1$

    public static final String KEY_NOT_FOUND_SUFFIX = "!!!"; //$NON-NLS-1$

    public static final String SINGLE_QUOTE = "'"; //$NON-NLS-1$

    public static final String SINGLE_QUOTE_MUTI = "''"; //$NON-NLS-1$

    /**
     * Returns the i18n formatted message for <i>key</i> in the specified bundle.
     * 
     * @param key - the key for the desired string
     * @param resourceBundle - the ResourceBundle to search in
     * @return the string for the given key in the given resource bundle
     */
    public static String getString(String key, ResourceBundle resourceBundle) {
        if (resourceBundle == null) {
            return KEY_NOT_FOUND_PREFIX + key + KEY_NOT_FOUND_SUFFIX;
        }
        log.debug("Getting key" + key + "in" + resourceBundle.toString()); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            return resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return KEY_NOT_FOUND_PREFIX + key + KEY_NOT_FOUND_SUFFIX;
        }
    }

    /**
     * Returns the i18n formatted message for <i>key</i> and <i>args</i> in the specified bundle.
     * 
     * @param key - the key for the desired string
     * @param resourceBundle - the ResourceBundle to search in
     * @param args - arg to include in the string
     * @return the string for the given key in the given resource bundle
     */
    public static String getString(String key, ResourceBundle resourceBundle, Object... args) {
        return MessageFormat.format(getString(key, resourceBundle).replaceAll(SINGLE_QUOTE, SINGLE_QUOTE_MUTI), args);
    }
}
