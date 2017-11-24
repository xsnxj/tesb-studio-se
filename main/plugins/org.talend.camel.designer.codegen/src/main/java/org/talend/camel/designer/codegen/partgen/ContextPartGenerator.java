package org.talend.camel.designer.codegen.partgen;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.camel.designer.codegen.argument.ArgumentBuilderHolder;
import org.talend.camel.designer.codegen.argument.CodeGeneratorArgumentBuilder;
import org.talend.camel.designer.codegen.jet.JetUtil;
import org.talend.commons.utils.PasswordEncryptUtil;
import org.talend.core.model.process.IContext;
import org.talend.core.model.process.IContextParameter;
import org.talend.core.model.utils.ContextParameterUtils;
import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.designer.codegen.config.EInternalTemplate;
import org.talend.designer.codegen.config.JetBean;
import org.talend.designer.codegen.config.TemplateUtil;
import org.talend.designer.codegen.exception.CodeGeneratorException;
import org.talend.designer.codegen.model.CodeGeneratorInternalTemplatesFactoryProvider;

public class ContextPartGenerator extends ArgumentBuilderHolder implements PartGenerator<IContext> {

	private static Logger log = Logger.getLogger(ContextPartGenerator.class);

	public ContextPartGenerator(CodeGeneratorArgumentBuilder argumentBuilder) {
		super(argumentBuilder);
	}

	@Override
	public CharSequence generatePart(IContext designerContext, Object... ignoreParamsP) throws CodeGeneratorException {
		return generatePart(designerContext);
	}

	public CharSequence generatePart(IContext designerContext) throws CodeGeneratorException {
		List<IContextParameter> listParameters = designerContext.getContextParameterList();
		if (listParameters == null) {
			return "";
		}
		CodeGeneratorArgument codeGenArgument = argumentBuilder.build();

		codeGenArgument.setContextName(designerContext.getName());

		List<IContextParameter> listParametersCopy = tranformEncryptedParamsAndHandleEscape(listParameters);
		codeGenArgument.setNode(listParametersCopy);

		JetBean jetBean = JetUtil.createJetBean(codeGenArgument);

        for (TemplateUtil template : CodeGeneratorInternalTemplatesFactoryProvider.getInstance().getTemplatesFromType(
                EInternalTemplate.CONTEXT)) {
            jetBean.setJetPluginRepository(template.getJetPluginRepository());
            jetBean.setTemplateRelativeUri(template.getTemplateRelativeUri());
        }
		return JetUtil.jetGenerate(jetBean);
	}

	private static List<IContextParameter> tranformEncryptedParamsAndHandleEscape(List<IContextParameter> listParameters) {
		 List<IContextParameter> listParametersCopy = new ArrayList<IContextParameter>(listParameters.size());

	        // encrypt the password
	        for (IContextParameter iContextParameter : listParameters) {
	            IContextParameter icp = iContextParameter.clone();
	            if (PasswordEncryptUtil.isPasswordType(iContextParameter.getType())) {
	                String pwd = icp.getValue();
	                if (pwd != null && !pwd.isEmpty()) {
	                    try {
	                        icp.setValue(PasswordEncryptUtil.encryptPasswordHex(pwd));
	                    } catch (Exception e) {
	                        log.error(e.getMessage(), e);
	                    }
	                }
	            } else {
	                ContextParameterUtils.unescapeValue(icp);
	            }
	            listParametersCopy.add(icp);
	        }
		return listParametersCopy;
	}
}
