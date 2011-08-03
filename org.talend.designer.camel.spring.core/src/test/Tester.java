package test;

import java.util.Map;

import org.talend.designer.camel.spring.core.CamelSpringParser;
import org.talend.designer.camel.spring.core.ISpringParserListener;

public class Tester implements ISpringParserListener{

//	private static final String FILE_PATH = "tests/BigFileFixedThreadPoolTest.xml";
//	private static final String FILE_PATH = "tests/BigFileCachedThreadPoolTest.xml";
//	private static final String FILE_PATH = "tests/BigFileParallelTest.xml";
//	private static final String FILE_PATH = "tests/loadbalancer.xml";
	private static final String FILE_PATH = "tests/activemq/camel-context.xml";
//	private static final String FILE_PATH = "tests/others/camel-fliproute.xml";
//	private static final String FILE_PATH = "tests/others/routingslip-header.xml";
//	private static final String FILE_PATH = "tests/xmlOrderNamespace.xml";
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CamelSpringParser camelSpringParser = new CamelSpringParser();
		camelSpringParser.addListener(new Tester());
		camelSpringParser.startParse(FILE_PATH);
	}
	
	public void preProcess() {
		
	}

	public void process(int componentType, Map<String, String> parameters,
			int connectionType, String sourceId, Map<String, String> connParameters) {
		System.out.println(componentType);
		System.out.println(parameters);
		System.out.println(connectionType);
		System.out.println(sourceId);
	}

	public void postProcess() {
		
	}
	

}
