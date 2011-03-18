package org.talend.designer.esb.webservice.ws;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.transform.TransformerException;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlutil.ServiceDiscoveryHelper;
import org.talend.ws.helper.ServiceInvokerHelper;

/**
 * DOC gcui class global comment. Detailled comment
 */
public class WSDLDiscoveryHelper2 extends ServiceInvokerHelper {

    public static List<Function> functionsAvailable;

    public static List<ParameterInfo> inputParameters;

    public static List<ParameterInfo> outputParameters;

    private Vector wsdlTypes = new Vector();

    /**
     * DOC gcui Comment method "getFunctionsAvailable".
     * 
     * @param wsdlURI
     * @return
     * @throws URISyntaxException
     * @throws TransformerException
     */
    public static List<Function> getFunctionsAvailable(String wsdlURI) {
        functionsAvailable = new ArrayList();
        ServiceDiscoveryHelper sdh;
        try {
            sdh = new ServiceDiscoveryHelper(wsdlURI);
            Definition def = sdh.getDefinition();

            // wsdlTypes = createSchemaFromTypes(def);

            def.getTypes();
            Map m1 = def.getBindings();
            Set<javax.xml.namespace.QName> h1 = m1.keySet();
            for (javax.xml.namespace.QName s : h1) {
                System.out.println(m1.get(s));
            }
            System.out.println("#################################################################");
            Map m2 = def.getServices();
            Set<javax.xml.namespace.QName> h2 = m2.keySet();
            for (javax.xml.namespace.QName s : h2) {
                System.out.println(m2.get(s));
            }
            System.out.println("#################################################################");
            Map m3 = def.getPortTypes();
            Set<javax.xml.namespace.QName> h3 = m3.keySet();
            for (javax.xml.namespace.QName s : h3) {
                System.out.println(m3.get(s));
            }

            XmlSchemaCollection xsc = sdh.getSchema();
            XmlSchema[] xs = xsc.getXmlSchemas();
            // for (String s : xs[0].getAttributes().getNames()) {
            // s
            // }
        } catch (WSDLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return functionsAvailable;
    }

    public static void main(String[] args) {

        List<Function> test = new ArrayList<Function>();
        test = getFunctionsAvailable("C:/Documents and Settings/Administrator/桌面/myServiceTest.wsdl");
    }

}
