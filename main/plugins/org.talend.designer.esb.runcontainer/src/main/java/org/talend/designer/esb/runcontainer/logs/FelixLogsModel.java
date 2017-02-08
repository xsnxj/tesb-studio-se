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
package org.talend.designer.esb.runcontainer.logs;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class FelixLogsModel {

    private int id;

    private long received;

    private String level;

    private int raw_level;

    private String message;

    private String service;

    private String exception;

    private int bundleId;

    private String bundleName;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getReceived() {
        return this.received;
    }

    public void setReceived(long received) {
        this.received = received;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getRaw_level() {
        return this.raw_level;
    }

    public void setRaw_level(int raw_level) {
        this.raw_level = raw_level;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getService() {
        return this.service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getException() {
        return this.exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getBundleId() {
        return this.bundleId;
    }

    public void setBundleId(int bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundleName() {
        return this.bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }
}
