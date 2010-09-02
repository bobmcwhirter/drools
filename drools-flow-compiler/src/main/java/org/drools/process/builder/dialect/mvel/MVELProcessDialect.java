package org.drools.process.builder.dialect.mvel;

import org.drools.process.builder.ActionBuilder;
import org.drools.process.builder.ProcessBuildContext;
import org.drools.process.builder.ProcessClassBuilder;
import org.drools.process.builder.ReturnValueEvaluatorBuilder;
import org.drools.process.builder.dialect.ProcessDialect;

public class MVELProcessDialect implements ProcessDialect {

	private static final ActionBuilder actionBuilder = new MVELActionBuilder();
	private static final ReturnValueEvaluatorBuilder returnValueBuilder = new MVELReturnValueEvaluatorBuilder();
	
	public void addProcess(final ProcessBuildContext context) {
        // @TODO setup line mappings
	}

	public ActionBuilder getActionBuilder() {
		return actionBuilder;
	}

	public ProcessClassBuilder getProcessClassBuilder() {
        throw new UnsupportedOperationException( "MVELProcessDialect.getProcessClassBuilder is not supported" );
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
		return returnValueBuilder;
	}

}
