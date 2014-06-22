package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class FTPComponentParser extends AbstractComponentParser {

	public FTPComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		String uri = null;
		if (oid instanceof FromDefinition) {
			uri = ((FromDefinition) oid).getUri();
		} else if (oid instanceof ToDefinition) {
			uri = ((ToDefinition) oid).getUri();
		}
		/*
		 * uri format:
		 * ftp://[username@]hostname[:port]/directoryname[?options]
		 * sftp://[username@]hostname[:port]/directoryname[?options]
		 * ftps://[username@]hostname[:port]/directoryname[?options]
		 */
		if (uri != null) {
			//schema
			int index = uri.indexOf(":");
			if (index != -1) {
				map.put(FTP_SCHEMA_TYPE, uri.substring(0, index));
				uri = uri.substring(index+1);
			}
			if(uri.startsWith("//")){
				uri = uri.substring(2);
			}
			
			//username
			index = uri.indexOf("@");
			if(index!=-1){
				map.put(FTP_USERNAME, uri.substring(0,index));
				uri = uri.substring(index+1);
			}
			
			String majorPart = uri;
			String parameters = "";
			index = uri.indexOf("?");
			if(index != -1){
				majorPart = uri.substring(0,index);
				parameters = uri.substring(index+1);
			}
			
			//server
			index = majorPart.indexOf(":");
			if(index!=-1){
				map.put(FTP_SERVER, majorPart.substring(0,index));
				majorPart = majorPart.substring(index+1);
				//port
				index = majorPart.indexOf("/");
				if(index!=-1){
					map.put(FTP_PORT, majorPart.substring(0,index));
					map.put(FTP_DIRECTORY, majorPart.substring(index+1));
				}else{
					map.put(FTP_PORT, majorPart);
				}
			}else{
				index = majorPart.indexOf("/");
				if(index == -1){
					map.put(FTP_SERVER, majorPart);
				}else{
					map.put(FTP_SERVER, majorPart.substring(0,index));
					map.put(FTP_DIRECTORY, majorPart.substring(index+1));
				}
			}
			
			//parameters
			String[] ps = parameters.split("&");
			for (String s : ps) {
				String[] kv = s.split("=");
				if (kv.length == 2) {
					map.put(kv[0], kv[1]);
				}
			}
		}
	}

	@Override
	public int getType() {
		return FTP;
	}

}
