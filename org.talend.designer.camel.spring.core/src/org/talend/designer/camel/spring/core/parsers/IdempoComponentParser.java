package org.talend.designer.camel.spring.core.parsers;

import java.io.File;
import java.util.Map;

import org.apache.camel.model.IdempotentConsumerDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues.ValueHolder;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class IdempoComponentParser extends AbstractComponentParser {

	public IdempoComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		IdempotentConsumerDefinition icd = (IdempotentConsumerDefinition) oid;
		IdempotentRepository<?> messageIdRepository = icd.getMessageIdRepository();
		String filePath = null;
		String cacheSize = "0";
		String beanClassName = null;
		if(messageIdRepository!=null){
			beanClassName = messageIdRepository.getClass().getName();
			if(messageIdRepository instanceof FileIdempotentRepository){
				filePath = ((FileIdempotentRepository)messageIdRepository).getFilePath();
				cacheSize = ((FileIdempotentRepository)messageIdRepository).getCacheSize()+"";
			}else if(messageIdRepository instanceof MemoryIdempotentRepository){
				cacheSize = ((MemoryIdempotentRepository)messageIdRepository).getCacheSize()+"";
			}
		}else{
			String ref = icd.getMessageIdRepositoryRef();
			BeanDefinition beanDefinition = getBeanDefinition(ref);
			beanClassName = beanDefinition.getBeanClassName();
			ConstructorArgumentValues argumentValues = beanDefinition.getConstructorArgumentValues();
			ValueHolder sizeValue = argumentValues.getGenericArgumentValue(int.class);
			if(sizeValue!=null){
				Object value = sizeValue.getValue();
				if(value!=null){
					TypedStringValue v = (TypedStringValue) value;
					cacheSize = v.getValue();
				}
			}
			ValueHolder fileValue = argumentValues.getGenericArgumentValue(File.class);
			if(fileValue!=null){
				Object value = fileValue.getValue();
				if(value!=null){
					TypedStringValue v = (TypedStringValue) value;
					filePath = v.getValue();
				}
			}
		}
		if(beanClassName!=null){
			if(FileIdempotentRepository.class.getName().equals(beanClassName)){
				map.put(ID_REPOSITORY_TYPE, ID_FILE_REPOSITORY);
				map.put(ID_FILE_STORE, "\""+filePath+"\"");
				map.put(ID_CACHE_SIZE, cacheSize+"");
			}else if(MemoryIdempotentRepository.class.getName().equals(beanClassName)){
				map.put(ID_REPOSITORY_TYPE, ID_MEMORY_REPOSITORY);
				map.put(ID_CACHE_SIZE, cacheSize+"");
			}
		}
		ExpressionDefinition expression = icd.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor.getExpressionMap(expression);
		map.putAll(expressionMap);
	}

	@Override
	public int getType() {
		return IDEM;
	}

}
