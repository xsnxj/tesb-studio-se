package org.talend.designer.camel.spring.core.parsers;

import java.util.Map;

import org.apache.camel.ExchangePattern;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.SetExchangePatternDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class ExchangePatternComponentParser extends AbstractComponentParser {

	public ExchangePatternComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		SetExchangePatternDefinition sepd = (SetExchangePatternDefinition) oid;
		ExchangePattern pattern = sepd.getPattern();
		String type = null;
		if(ExchangePattern.InOnly==pattern){
			type = EX_INONLY;
		}else if(ExchangePattern.InOptionalOut==pattern){
			type = EX_INOPTIONOUT;
		}else if(ExchangePattern.InOut==pattern){
			type = EX_INOUT;
		}else if(ExchangePattern.OutIn==pattern){
			type = EX_OUTIN;
		}else if(ExchangePattern.OutOnly == pattern){
			type = EX_OUTONLY;
		}else if(ExchangePattern.OutOptionalIn==pattern){
			type = EX_OUTOPTIONALIN;
		}else if(ExchangePattern.RobustInOnly==pattern){
			type = EX_ROBUST_INONLY;
		}else if(ExchangePattern.RobustOutOnly==pattern){
			type = EX_ROBUSTOUTONLY;
		}
		if(type!=null){
			map.put(EX_EXCHANGE_TYPE, type);
		}
	}

	@Override
	public int getType() {
		return PATTERN;
	}

}
