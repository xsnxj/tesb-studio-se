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
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.talend.designer.esb.runcontainer.core.ESBRunContainerPlugin;
import org.talend.designer.esb.runcontainer.preferences.RunContainerPreferenceInitializer;
import org.talend.designer.esb.runcontainer.server.RuntimeServerController;
import org.talend.designer.esb.runcontainer.ui.console.RuntimeClient;

public class RuntimeConsoleUtil {

    public static final String KARAF_CONSOLE = "ESB Runtime";

    private static RuntimeServerController server;

    private static String systemCommand = null;

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

        Thread consoleThread = new Thread("Runtime Console 1") {

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
                }
            }
        };
        consoleThread.start();

        Thread connectThread = new Thread("Runtime Console 2") {

            public void run() {
                IPreferenceStore store = ESBRunContainerPlugin.getDefault().getPreferenceStore();
                String etcLocation = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_LOCATION);
                String host = store.getString(RunContainerPreferenceInitializer.P_ESB_RUNTIME_HOST);
                System.setProperty("karaf.etc", etcLocation + "/etc");
                String[] karafArgs = new String[] { "-h", host };

                try {
                    m.connect(karafArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        connectThread.start();
    }

    public static void startLocalRuntime(String karafHome) {
        // find bin

    }

    // test
    public static void main(String[] args) {
        startLocalRuntime("E:\\nb\\Talend-ESB-V6.3.1\\container");
    }

    public static void exec(String cmd) {
        try {
            findConsole(KARAF_CONSOLE).newOutputStream().write(cmd + '\n');
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // systemCommand = cmd;
    }
}
