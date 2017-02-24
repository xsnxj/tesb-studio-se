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
package org.talend.designer.esb.runcontainer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.talend.commons.ui.runtime.exception.ExceptionHandler;

/**
 * DOC yyi class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class SocketUtil {

    public static void main(String[] args) throws Exception {

        Socket processSocket = null;
        ServerSocket serverSock = null;
        int statsPort = 3658;
        do {
            try {
                serverSock = new ServerSocket(statsPort);
                processSocket = serverSock.accept();

            } catch (IOException e) {
                ExceptionHandler.process(e);
            } finally {
                try {
                    if (serverSock != null) {
                        serverSock.close();
                    }
                } catch (IOException e1) {
                    // e1.printStackTrace();
                    ExceptionHandler.process(e1);
                }
            }
        } while (processSocket == null);

        if (processSocket != null) {
            InputStream in = processSocket.getInputStream();
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));

            String line = reader.readLine();
            System.out.println("------>" + line);
        }
    }
}
