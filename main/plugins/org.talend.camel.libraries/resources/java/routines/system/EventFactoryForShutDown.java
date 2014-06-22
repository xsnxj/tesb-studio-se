package routines.system;
import java.util.EventObject;
import org.apache.camel.Exchange;
import org.apache.camel.management.DefaultEventFactory;

public class EventFactoryForShutDown extends DefaultEventFactory
{
	
	MonitorThread monitorThread ;
	
	public MonitorThread getMonitorThread() {
		return monitorThread;
	}

	public void setMonitorThread(MonitorThread monitorThread) {
		this.monitorThread = monitorThread;
	}

	@Override
	public EventObject createExchangeCompletedEvent(Exchange exchange) {
		synchronized (monitorThread){
			monitorThread.getExchangeId().remove(exchange.getExchangeId());
			monitorThread.notifyAll(); //to notify the shutdownManage thread.
		}
		
		return super.createExchangeCompletedEvent(exchange);
	}

	@Override
	public EventObject createExchangeCreatedEvent(Exchange exchange) {
			monitorThread.getExchangeId().add(exchange.getExchangeId());
			return super.createExchangeCreatedEvent(exchange);
	}
}