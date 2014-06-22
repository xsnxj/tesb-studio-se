package org.talend.designer.camel.spring.core;

import org.apache.camel.Processor;
import org.apache.camel.model.RouteDefinition;

public class UnsupportedElementException extends Exception {

	private static final long serialVersionUID = 4880852054698799574L;

	public UnsupportedElementException(Processor processor) {
		super("Unsupported element exist: " + processor.toString());
	}

	public UnsupportedElementException(RouteDefinition route) {
		super("Unsupported element exist: " + route.toString());
	}
}
