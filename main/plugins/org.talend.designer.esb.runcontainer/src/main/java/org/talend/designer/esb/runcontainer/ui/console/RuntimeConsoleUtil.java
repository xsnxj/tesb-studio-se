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
package org.talend.designer.esb.runcontainer.ui.console;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

public class RuntimeConsoleUtil {

    private static final String KARAF_CONSOLE = "Runtime Console";

    public static IOConsole findConsole(String name) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = (IConsole[]) conMan.getConsoles();
        for (int i = 0; i < existing.length; i++) {
            if (name.equals(existing[i].getName()))
                return (IOConsole) existing[i];
        }
        // no console found, so create a new one
        IOConsole myConsole = new IOConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });
        return myConsole;
    }

    public static OutputStream getOutputStream() {
        return findConsole(KARAF_CONSOLE).newOutputStream();
    }

    public static void clearConsole() {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = (IConsole[]) conMan.getConsoles();
        for (int i = 0; i < existing.length; i++) {
            if (KARAF_CONSOLE.equals(existing[i].getName())) {
                ((IOConsole) existing[i]).destroy();
                conMan.removeConsoles(new IConsole[] { existing[i] });
            }
        }
    }

    public static void loadConsole() {
        clearConsole();
        RuntimeClient m = new RuntimeClient();

        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream();

        try {
            pos.connect(pis);
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        m.setInputStream(pis);

        new Thread() {

            public void run() {
                InputStream is = findConsole(KARAF_CONSOLE).getInputStream();

                int count = 0;
                byte[] bs = new byte[1024];
                try {
                    while ((count = is.read(bs)) > 0) {
                        pos.write(new String(bs, 0, count).getBytes());
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        is.close();
                        pos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

        new Thread() {

            public void run() {
                System.setProperty("karaf.etc", "");
                String[] karafArgs = new String[] { "-h", "localhost" };

                try {
                    m.connect(karafArgs);
                } catch (Exception eee) {
                    eee.printStackTrace();
                }
            }
        }.start();
    }
}
