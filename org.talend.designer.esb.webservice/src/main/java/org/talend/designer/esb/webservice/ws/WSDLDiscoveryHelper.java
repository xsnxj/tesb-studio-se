package org.talend.designer.esb.webservice.ws;

import java.util.ArrayList;
import java.util.List;

import javax.wsdl.Definition;

import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.commons.ui.runtime.exception.MessageBoxExceptionHandler;
import org.talend.core.model.utils.TalendTextUtils;
import org.talend.designer.esb.webservice.ws.wsdlinfo.Function;
import org.talend.designer.esb.webservice.ws.wsdlinfo.OperationInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ParameterInfo;
import org.talend.designer.esb.webservice.ws.wsdlinfo.ServiceInfo;
import org.talend.designer.esb.webservice.ws.wsdlutil.ComponentBuilder;
import org.talend.designer.esb.webservice.ws.wsdlutil.ServiceHelperConfiguration;

/**
 * 
 * @author gcui
 */
public class WSDLDiscoveryHelper {

    public List<Function> functionsAvailable;

    public List<ParameterInfo> inputParameters;

    public List<ParameterInfo> outputParameters;

    private String exceptionMessage;

	private Definition def;

    /**
     * DOC gcui Comment method "getFunctionsAvailable".
     * 
     * @param wsdlURI
     * @return
     * @throws Exception 
     */
    public List<Function> getFunctionsAvailable(String wsdlURI, ServiceHelperConfiguration config) throws Exception {
        functionsAvailable = new ArrayList<Function>();
        wsdlURI = TalendTextUtils.removeQuotes(wsdlURI);

        try {
            ComponentBuilder builder = new ComponentBuilder();
            ServiceInfo serviceInput = new ServiceInfo(wsdlURI, config);
            ServiceInfo[] services = builder.buildserviceinformation(serviceInput);
            def = builder.getDefinition();
//            types = builder.

            exceptionMessage = builder.getExceptionMessage();
            for (ServiceInfo serviceInfo : services) {
                for (OperationInfo oper : serviceInfo.getOperations()) {
                    Function f = new Function(serviceInfo, oper);
                    functionsAvailable.add(f);
                }
            }
        } catch (Exception e) {
            exceptionMessage = exceptionMessage + e.getMessage();
            ExceptionHandler.process(e);
            throw e;
        }

        if (!"".equals(exceptionMessage)) {
            Exception e = new Exception(exceptionMessage);
            MessageBoxExceptionHandler.process(e);
        }
        return functionsAvailable;
    }

    public List<Function> getFunctionsAvailable(String wsdlURI) throws Exception {
        return getFunctionsAvailable(wsdlURI, null);
    }

	public Definition getDefinition() {
		return def;
	}
}
