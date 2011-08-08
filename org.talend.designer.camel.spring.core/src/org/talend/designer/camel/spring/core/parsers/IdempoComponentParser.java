package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.IdempotentConsumerDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
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
		if(messageIdRepository!=null){
			if(messageIdRepository instanceof FileIdempotentRepository){
				map.put(ID_REPOSITORY_TYPE, ID_FILE_REPOSITORY);
				FileIdempotentRepository fir = (FileIdempotentRepository) messageIdRepository;
				String filePath = fir.getFilePath();
				int cacheSize = fir.getCacheSize();
				map.put(ID_FILE_STORE, filePath);
				map.put(ID_CACHE_SIZE, cacheSize+"");
			}else if(messageIdRepository instanceof MemoryIdempotentRepository){
				map.put(ID_REPOSITORY_TYPE, ID_MEMORY_REPOSITORY);
				MemoryIdempotentRepository mir = (MemoryIdempotentRepository) messageIdRepository;
				int cacheSize = mir.getCacheSize();
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
