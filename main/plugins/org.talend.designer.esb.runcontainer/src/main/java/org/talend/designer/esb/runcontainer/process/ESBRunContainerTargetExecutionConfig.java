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
package org.talend.designer.esb.runcontainer.process;

import org.talend.core.model.process.IServerConfiguration;
import org.talend.core.model.process.ITargetExecutionConfig;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class ESBRunContainerTargetExecutionConfig implements ITargetExecutionConfig {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IServerConfiguration#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IServerConfiguration#getHost()
     */
    @Override
    public String getHost() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IServerConfiguration#getPort()
     */
    @Override
    public int getPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IServerConfiguration#getUser()
     */
    @Override
    public String getUser() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.IServerConfiguration#getPassword()
     */
    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#isRemote()
     */
    @Override
    public boolean isRemote() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#getFileTransferPort()
     */
    @Override
    public int getFileTransferPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#setFileTransferPort(int)
     */
    @Override
    public void setFileTransferPort(int transferFilePort) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#getCommandlineServerConfig()
     */
    @Override
    public IServerConfiguration getCommandlineServerConfig() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.core.model.process.ITargetExecutionConfig#setCommandlineServerConfig(org.talend.core.model.process
     * .IServerConfiguration)
     */
    @Override
    public void setCommandlineServerConfig(IServerConfiguration cmdLineServer) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#setUseSSL(boolean)
     */
    @Override
    public void setUseSSL(boolean useSSL) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#useSSL()
     */
    @Override
    public boolean useSSL() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#isUseJMX()
     */
    @Override
    public boolean isUseJMX() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#setUseJMX(boolean)
     */
    @Override
    public void setUseJMX(boolean useJMX) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#getRemotePort()
     */
    @Override
    public int getRemotePort() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#setRemotePort(int)
     */
    @Override
    public void setRemotePort(int remotePort) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#getRunAsUser()
     */
    @Override
    public String getRunAsUser() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.model.process.ITargetExecutionConfig#setRunAsUser(java.lang.String)
     */
    @Override
    public void setRunAsUser(String runAsUser) {
        // TODO Auto-generated method stub

    }

}
