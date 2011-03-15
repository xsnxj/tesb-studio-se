package routines.system;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.CamelContext;

/**
 * Author paulJin create 2011-03-04
 */
public class MonitorThread extends Thread {

    int detectForNewExchangeInterval;

    int waitForFirstExchange;

    public int getWaitForFirstExchange() {
        return waitForFirstExchange;
    }

    public void setWaitForFirstExchange(int waitForFirstExchange) {
        this.waitForFirstExchange = waitForFirstExchange;
    }

    int i = 0;

    Set<String> exchangeId = new HashSet<String>();

    CamelContext context;

    public CamelContext getContext() {
        return context;
    }

    public void setContext(CamelContext context) {
        this.context = context;
    }

    public Set<String> getExchangeId() {
        return exchangeId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this) {
                    if (exchangeId.size() == 0 && i > 0) {

                        if (waitForNextExchange())
                            break;
                        else {
                            this.notifyAll();
                            this.wait();
                        }
                    } else {
                        if(i==0)
                        Thread.sleep(waitForFirstExchange);
                        i++;
                        if (waitForNextExchange())
                            break;
                        else {
                            this.notifyAll();
                            this.wait();
                        }
                    }

                }

            }
            context.stop();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean waitForNextExchange() throws InterruptedException {
        int initDelay = 1;
        boolean isComplete = true;
        while (true) {
            Thread.sleep(initDelay);
            initDelay = initDelay * 10;
            if (exchangeId.size() != 0) {
                isComplete = false;
                break;
            } else if (initDelay < detectForNewExchangeInterval) {
                continue;
            } else {
                isComplete = true;
                break;
            }
        }
        return isComplete;
    }

    public void setDetectForNewExchangeInterval(int detectForNewExchangeInterval) {
        this.detectForNewExchangeInterval = detectForNewExchangeInterval;
    }
}
