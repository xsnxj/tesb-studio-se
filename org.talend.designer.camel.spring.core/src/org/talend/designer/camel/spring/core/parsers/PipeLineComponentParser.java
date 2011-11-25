package org.talend.designer.camel.spring.core.parsers;

import java.util.List;
import java.util.Map;

import org.apache.camel.model.BeanDefinition;
import org.apache.camel.model.OptionalIdentifiedDefinition;
import org.apache.camel.model.PipelineDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.ToDefinition;
import org.talend.designer.camel.spring.core.intl.XmlFileApplicationContext;

public class PipeLineComponentParser extends AbstractComponentParser {

	public PipeLineComponentParser(XmlFileApplicationContext appContext) {
		super(appContext);
	}

	@Override
	protected void parse(OptionalIdentifiedDefinition oid,
			Map<String, String> map) {
		PipelineDefinition pd = (PipelineDefinition) oid;
		List<ProcessorDefinition> outputs = pd.getOutputs();
		StringBuilder sb = new StringBuilder();
		for(ProcessorDefinition out:outputs){
			if(out instanceof ToDefinition){
				sb.append(((ToDefinition)out).getUri());
			}else if(out instanceof BeanDefinition){
				BeanDefinition bd = (BeanDefinition) out;
				sb.append(bd.getShortName());
				sb.append(":");
				sb.append(bd.getRef());
			}else{
				sb.append(out.toString());
			}
			sb.append(";");
		}
		map.put(PF_DESTINATIONS, sb.toString());
	}

	@Override
	public int getType() {
		return PF;
	}

}
