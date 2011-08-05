package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.IdempotentConsumerDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;

public class IdempoComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		IdempotentConsumerDefinition icd = (IdempotentConsumerDefinition) oid;
		IdempotentRepository<?> messageIdRepository = icd.getMessageIdRepository();
		if(messageIdRepository!=null){
			if(messageIdRepository instanceof FileIdempotentRepository){
				map.put(REPOSITORY_TYPE, FILE_REPOSITORY);
				FileIdempotentRepository fir = (FileIdempotentRepository) messageIdRepository;
				String filePath = fir.getFilePath();
				int cacheSize = fir.getCacheSize();
				map.put(FILE_STORE, filePath);
				map.put(CACHE_SIZE, cacheSize+"");
			}else if(messageIdRepository instanceof MemoryIdempotentRepository){
				map.put(REPOSITORY_TYPE, MEMORY_REPOSITORY);
				MemoryIdempotentRepository mir = (MemoryIdempotentRepository) messageIdRepository;
				int cacheSize = mir.getCacheSize();
				map.put(CACHE_SIZE, cacheSize+"");
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
