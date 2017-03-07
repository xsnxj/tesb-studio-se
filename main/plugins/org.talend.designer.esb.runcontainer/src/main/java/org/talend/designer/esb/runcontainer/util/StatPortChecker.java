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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.IPath;

/**
 * DOC yyan class global comment. Detailled comment <br/>
 *
 * $Id$
 *
 */
public class StatPortChecker implements Runnable {

    private static final String UTF_8 = "UTF-8";

    private static final String PORT_JOB_DEFAULT = "portStats =";

    // private static final String PORT_ROUTE_DEFAULT = "portStats = -1";

    private IPath path;

    private String statCode;

    private boolean stop = false;

    /**
     * @param path
     * @param statPort
     * @param tracePort
     */
    public StatPortChecker(IPath path, int statPort, int tracePort) {
        this.path = path;
        this.statCode = "portStats = " + statPort;
    }

    @Override
    public void run() {
        long modified = 0;
        File code = path.toFile();
        while (!stop) {
            if (code.exists() && code.lastModified() != modified) {
                try {
                    byte[] encoded = Files.readAllBytes(code.toPath());
                    String codeStr = new String(encoded, UTF_8);
                    int pos = codeStr.indexOf(PORT_JOB_DEFAULT);
                    if (pos > 0) {
                        int nbStart = codeStr.indexOf(";", pos);
                        FileWriter writer = new FileWriter(code);
                        BufferedWriter bufferedWriter = new BufferedWriter(writer);
                        bufferedWriter.write(codeStr.substring(0, pos) + statCode + codeStr.substring(nbStart, codeStr.length()));
                        bufferedWriter.flush();
                        bufferedWriter.close();
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    modified = code.lastModified();
                }
            }
        }
    }

    public void stop() {
        stop = true;
    }
}
