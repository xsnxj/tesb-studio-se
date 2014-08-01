package org.talend.camel.designer.codegen.partgen;

import org.talend.designer.codegen.exception.CodeGeneratorException;

public interface PartGenerator<T> {

	public abstract CharSequence generatePart(T paramT, Object... params) throws CodeGeneratorException;

}