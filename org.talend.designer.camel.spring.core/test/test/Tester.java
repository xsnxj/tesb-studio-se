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
//	private static final String FILE_PATH = "tests/blueprint/test.xml";
//	private static final String FILE_PATH = "F:/StudyResources/Camel/camelinaction-source/chapter12/tracer/src/test/resources/camelinaction/TracerSpringTest.xml";
//	private static final String FILE_PATH = "tests/activemq/wireTap.xml";
//	private static final String FILE_PATH = "tests/activemq/dynamic.xml";
//	private static final String FILE_PATH = "tests/activemq/pipes.xml";
//	private static final String FILE_PATH = "tests/activemq/cxf.xml";
//	private static final String FILE_PATH = "tests/activemq/setHeader.xml";
//	private static final String FILE_PATH = "tests/activemq/enrich.xml";
//	private static final String FILE_PATH = "tests/activemq/aggregate.xml";
//	private static final String FILE_PATH = "E:/work/items/SpringImport/tests/activemq/camel-context.xml";
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
		System.out.println("----------------");
	}

	public void postProcess() {
		
	}
	

}
