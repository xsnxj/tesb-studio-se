package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.ExchangePattern;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.SetExchangePatternDefinition;

public class ExchangePatternComponentParser extends AbstractComponentParser {

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		SetExchangePatternDefinition sepd = (SetExchangePatternDefinition) oid;
		ExchangePattern pattern = sepd.getPattern();
		String type = null;
		if(ExchangePattern.InOnly==pattern){
			type = INONLY;
		}else if(ExchangePattern.InOptionalOut==pattern){
			type = INOPTIONOUT;
		}else if(ExchangePattern.InOut==pattern){
			type = INOUT;
		}else if(ExchangePattern.OutIn==pattern){
			type = OUTIN;
		}else if(ExchangePattern.OutOnly == pattern){
			type = OUTONLY;
		}else if(ExchangePattern.OutOptionalIn==pattern){
			type = OUTOPTIONALIN;
		}else if(ExchangePattern.RobustInOnly==pattern){
			type = ROBUST_INONLY;
		}else if(ExchangePattern.RobustOutOnly==pattern){
			type = ROBUSTOUTONLY;
		}
		if(type!=null){
			map.put(EXCHANGE_TYPE, type);
		}
	}

	@Override
	public int getType() {
		return PATTERN;
	}

}
