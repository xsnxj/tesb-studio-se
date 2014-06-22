package org.talend.designer.camel.spring.core.intl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.spi.NodeIdFactory;

public class SpringNodeIdFactory implements NodeIdFactory {

	protected Map<String, AtomicInteger> nodeCounters = new HashMap<String, AtomicInteger>();

	public String createId(OptionalIdentifiedDefinition<?> definition) {
		String key = definition.getShortName();
		return key + getNodeCounter(key).incrementAndGet();
	}

	/**
	 * Returns the counter for the given node key, lazily creating one if
	 * necessary
	 */
	protected synchronized AtomicInteger getNodeCounter(String key) {
		AtomicInteger answer = nodeCounters.get(key);
		if (answer == null) {
			answer = new AtomicInteger(0);
			nodeCounters.put(key, answer);
		}
		return answer;
	}

	/**
	 * Helper method for test purposes that allows tests to start clean (made
	 * protected to ensure that it is not called accidentally)
	 */
	protected synchronized void resetAllCounters() {
		for (AtomicInteger counter : nodeCounters.values()) {
			counter.set(0);
		}
		nodeCounters.clear();
	}

}
