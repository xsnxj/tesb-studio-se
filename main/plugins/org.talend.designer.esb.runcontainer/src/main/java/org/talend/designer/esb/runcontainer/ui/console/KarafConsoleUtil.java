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

public class KarafConsoleUtil {

    private static final String KARAF_CONSOLE = "Karaf Console";

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
        KarafClient m = new KarafClient();

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
                System.setProperty("karaf.etc",
                        "D:\\yyi.talendbj.esb\\git\\tesb-studio-se\\main\\plugins\\org.talend.designer.esb.runcontainer\\etc");
                String[] karafArgs = new String[] { "-h", "localhost" };

                try {
                    m.connect(karafArgs);
                    System.out.println("-------------- done");
                } catch (Exception eee) {
                    eee.printStackTrace();
                }
            }
        }.start();
    }
}
