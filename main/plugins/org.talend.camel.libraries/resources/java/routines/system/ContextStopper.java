package routines.system;

import java.util.ArrayList;

import org.apache.camel.CamelContext;
import org.apache.camel.management.LoggingEventNotifier;
import org.apache.camel.spi.EventNotifier;

/**
 * Author paulJin create 2011-03-04
 */
public class ContextStopper {

    ContextStopper contextStopper;

    CamelContext context;

    long timeOut = 100L;

    int detectForNewExchangeInterval = 100;

    int waitForFirstExchange = 3000;// wait for 3 second default;

    public int getWaitForFirstExchange() {
        return waitForFirstExchange;
    }

    public ContextStopper setWaitForFirstExchange(int waitForFirstExchange) {
        if (waitForFirstExchange > this.waitForFirstExchange)
            this.waitForFirstExchange = waitForFirstExchange;
        return contextStopper;
    }

    public ContextStopper(CamelContext context) {
        this.context = context;
        contextStopper = this;
    }

    public static ContextStopper getInstance(CamelContext context) {
        return new ContextStopper(context);
    }

    public void shutDownWhenAllExchangeComplete() {
        LoggingEventNotifier notifier = new LoggingEventNotifier();
        java.util.List<EventNotifier> L = new ArrayList<EventNotifier>();
        L.add(notifier);
        context.getManagementStrategy().setEventNotifiers(L);
        EventFactoryForShutDown eventFactory = new EventFactoryForShutDown();
        context.getShutdownStrategy().setTimeout(timeOut); // set the timeout
        context.getManagementStrategy().setEventFactory(eventFactory);
        MonitorThread monitor = new MonitorThread();
        monitor.setDetectForNewExchangeInterval(detectForNewExchangeInterval);
        monitor.setWaitForFirstExchange(waitForFirstExchange);
        monitor.setContext(context);
        monitor.start();
        eventFactory.setMonitorThread(monitor);

    }

    public long getTimeOut() {
        return timeOut;
    }

    public ContextStopper setTimeOut(long timeOut) {
        this.timeOut = timeOut;
        return contextStopper;
    }

    public long getDetectForNewExchangeInterval() {
        return detectForNewExchangeInterval;
    }

    public ContextStopper setDetectForNewExchangeInterval(int detectForNewExchangeInterval) {
        if (detectForNewExchangeInterval > this.detectForNewExchangeInterval)
            this.detectForNewExchangeInterval = detectForNewExchangeInterval;
        return contextStopper;
    }

}
