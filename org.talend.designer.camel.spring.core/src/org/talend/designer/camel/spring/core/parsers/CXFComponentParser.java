package org.talend.designer.camel.spring.core.parsers;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.ToDefinition;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.TypedStringValue;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class CXFComponentParser extends AbstractComponentParser {

	public CXFComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		String uri = "";
		if(oid instanceof ToDefinition){
			uri = ((ToDefinition)oid).getUri();
		}else if(oid instanceof FromDefinition){
			uri = ((FromDefinition)oid).getUri();
		}
		
		String prefix = "cxf:bean:";
		if(uri.startsWith(prefix)){
			uri = uri.substring(prefix.length());
		}else{
			return;
		}
		int index = uri.indexOf("?");
		String additionalParameters = "";
		if(index!=-1){
			additionalParameters = uri.substring(index+1);
			uri = uri.substring(0,index);
		}
		String bean = uri;
		if(bean.trim().equals("")){
			return;
		}
		BeanDefinition beanDefinition = getBeanDefinition(bean);
		if(beanDefinition==null){
			return;
		}
		MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
		PropertyValue[] properties = propertyValues.getPropertyValues();
		for(PropertyValue pv:properties){
			String name = pv.getName();
			Object value = pv.getValue();
			if(value instanceof Map){
				Map<?, ?> valueMap = (Map<?, ?>)value;
				Set<?> keySet = valueMap.keySet();
				Iterator<?> iterator = keySet.iterator();
				while(iterator.hasNext()){
					Object nextKey = iterator.next();
					Object nextValue = valueMap.get(nextKey);
					String k = nextKey.toString();
					String v = nextValue.toString();
					if(nextKey instanceof TypedStringValue){
						TypedStringValue tsv = (TypedStringValue) nextKey;
						k = tsv.getValue();
					}
					if(nextValue instanceof TypedStringValue){
						v = ((TypedStringValue)nextValue).getValue();
					}
					if(!"beanId".equals(k))
						map.put(k, v);
				}
			}else{
				//hide bus
				if(!"bus".equals(name))
					map.put(name, value.toString());
			}
		}
		
		//process additional parameter
		if(!"".equals(additionalParameters)){
			String[] splits = additionalParameters.split("&");
			for(String s:splits){
				String[] kv = s.split("=");
				map.put(kv[0], kv[1]);
			}
		}
	}

	@Override
	public int getType() {
		return CXF;
	}

}
