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
package org.talend.designer.esb.runcontainer.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.talend.designer.esb.runcontainer.ui.console.RuntimeClient;

public class RuntimeConsoleUtil {

    public static final String KARAF_CONSOLE = "ESB Runtime";

    public static IOConsole findConsole(String name) {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
        for (int i = 0; i < existing.length; i++) {
            if (name.equals(existing[i].getName()))
                return (IOConsole) existing[i];
        }
        // no console found, so create a new one
        IOConsole myConsole = new IOConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });
        return myConsole;
    }

    public static IOConsoleOutputStream getOutputStream() {
        IOConsoleOutputStream outputStream = findConsole(KARAF_CONSOLE).newOutputStream();
        outputStream.setEncoding(System.getProperty("sun.jnu.encoding", "UTF-8"));
        return outputStream;
    }

    public static void clearConsole() {
        ConsolePlugin plugin = ConsolePlugin.getDefault();
        IConsoleManager conMan = plugin.getConsoleManager();
        IConsole[] existing = conMan.getConsoles();
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

        Thread consoleThread = new Thread("Runtime Console Input") {

            @Override
            public void run() {
                InputStream is = findConsole(KARAF_CONSOLE).getInputStream();

                int count = 0;
                byte[] bs = new byte[1024];
                try {
                    while ((count = is.read(bs)) > 0) {
                        pos.write(Arrays.copyOf(bs, count));
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        pos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        consoleThread.start();

        Thread connectThread = new Thread("Runtime Console Connector") {

            @Override
            public void run() {
                String[] karafArgs = new String[] { "-h", "localhost" };
                try {
                    m.connect(karafArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        connectThread.start();
    }
}
