package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.RecipientListDefinition;
import org.apache.camel.model.language.ExpressionDefinition;
import org.talend.designer.camel.spring.core.exprs.ExpressionProcessor;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class RecipientListComponentParser extends AbstractComponentParser {

	public RecipientListComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		RecipientListDefinition rld = (RecipientListDefinition) oid;
		ExpressionDefinition expression = rld.getExpression();
		Map<String, String> expressionMap = ExpressionProcessor
				.getExpressionMap(expression);

		map.putAll(expressionMap);

		boolean ignoreInvalidEndpoints = rld.isIgnoreInvalidEndpoints();
		boolean parallelProcessing = rld.isParallelProcessing();
		boolean stopOnException = rld.isStopOnException();
		map.put(RL_IGNORE_INVALID, ignoreInvalidEndpoints + "");
		map.put(RL_PARELLEL_PROCESS, parallelProcessing + "");
		map.put(RL_STOP_ON_EXCEPTION, stopOnException + "");

		String delimiter = rld.getDelimiter();
		if (delimiter != null) {
			map.put(RL_DELIMITER, delimiter);
		}
	}

	@Override
	public int getType() {
		return RECIPIENT;
	}

}
