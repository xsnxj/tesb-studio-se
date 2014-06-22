package org.talend.designer.camel.spring.core.saver;

import java.util.Map;
import java.util.Set;

import org.talend.designer.camel.spring.core.models.SpringRouteNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpComponentSaver extends AbstractComponentSaver {

	public FtpComponentSaver(Document document, Element rootElement, Element contextElement) {
		super(document, rootElement, contextElement);
	}

	@Override
	/**
	 * generated xml format:
	 * <to uri="(ftp|ftps|sftp)://[username@]hostname[:port]/directoryname[?options]" />
	 * or
	 * <from uri="(ftp|ftps|sftp)://[username@]hostname[:port]/directoryname[?options]" />
	 */
	public Element save(SpringRouteNode srn, Element parent) {
		SpringRouteNode preNode = srn.getParent();
		
		Map<String, String> parameter = srn.getParameter();
		StringBuilder sb = new StringBuilder();
		
		String type = parameter.get(FTP_SCHEMA_TYPE);
		sb.append(type);
		sb.append("://");
		parameter.remove(FTP_SCHEMA_TYPE);
		
		String userName = parameter.get(FTP_USERNAME);
		if (userName != null && !"".equals(userName)) {
			sb.append(userName);
			sb.append("@");
		}
		parameter.remove(FTP_USERNAME);
		
		String server = parameter.get(FTP_SERVER);
		sb.append(server);
		parameter.remove(FTP_SERVER);
		
		String port = parameter.get(FTP_PORT);
		if (port != null && !"".equals(port)) {
			sb.append(":");
			sb.append(port);
		}
		parameter.remove(FTP_PORT);
		
		String directory = parameter.get(FTP_DIRECTORY);
		if (directory != null && !"".equals(directory)) {
			sb.append("/");
			sb.append(directory);
		}
		parameter.remove(FTP_DIRECTORY);
		
		Set<String> keySet = parameter.keySet();
		if(keySet.size()>0){
			sb.append("?");
		}
		String password = parameter.get("password");
		if (password != null && !"".equals(password)) {
			sb.append("password=");
			sb.append(removeQuote(password));
			sb.append("&");
			keySet.remove("password");
		}
		for(String s:keySet){
			String value = parameter.get(s);
			value = removeQuote(value);
			if(value==null||"".equals(value)){
				continue;
			}
			sb.append(removeQuote(s));
			sb.append("=");
			sb.append(value);
			sb.append("&");
		}
		if(sb.charAt(sb.length()-1)=='&'){
			sb.deleteCharAt(sb.length()-1);
		}
		
		Element element = null;
		if (preNode == null) {
			element = document.createElement(FROM_ELE);
		} else {
			element = document.createElement(TO_ELE);
		}
		element.setAttribute(URI_ATT, sb.toString());
		parent.appendChild(element);
		return element;
	}

}
