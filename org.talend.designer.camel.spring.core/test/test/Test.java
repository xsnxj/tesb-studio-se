package test;

import org.talend.designer.camel.spring.core.CamelSpringParser;

public class Test {

	public static void main(String[] args) throws Exception {
		CamelSpringParser camelSpringParser = new CamelSpringParser();
		// camelSpringParser.addListener(new ISpringParserListener() {
		//
		// public void process(int componentType, Map<String, String>
		// parameters,
		// int connectionType, String sourceId,
		// Map<String, String> connectionParameters) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void preProcess() {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// public void postProcess() {
		// // TODO Auto-generated method stub
		//
		// }
		// })
		camelSpringParser
				.startParse("D:/GreenTools/ftp/repository/org.talend.designer.camel.spring.core/output/camel-context.xml");
	}

}
