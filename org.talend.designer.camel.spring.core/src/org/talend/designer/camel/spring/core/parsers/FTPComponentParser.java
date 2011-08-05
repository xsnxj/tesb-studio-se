package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;

public class FTPComponentParser extends AbstractComponentParser {

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
			
			//server
			index = uri.indexOf(":");
			if(index!=-1){
				map.put(FTP_SERVER, uri.substring(0,index));
				uri = uri.substring(index+1);
				//port
				index = uri.indexOf("/");
				if(index!=-1){
					map.put(FTP_PORT, uri.substring(0,index));
					uri = uri.substring(index+1);
				}
			}else{
				index = uri.indexOf("/");
				if(index!=-1){
					map.put(FTP_SERVER, uri.substring(0,index));
					uri = uri.substring(index+1);
				}
			}
			
			//directory
			index = uri.indexOf("?");
			if(index==-1){
				map.put(FTP_DIRECTORY, uri);
			}else{
				map.put(FTP_DIRECTORY, uri.substring(0,index));
				//options
				uri = uri.substring(index+1);
				String[] parameters = uri.split("&");
				for (String s : parameters) {
					String[] kv = s.split("=");
					if (kv.length == 2) {
						map.put(kv[0], kv[1]);
					}
				}
			}
		}
	}

	@Override
	public int getType() {
		return FTP;
	}

}
