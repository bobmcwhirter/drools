package org.drools.process.builder;

import org.drools.compiler.ReturnValueDescr;
import org.drools.process.core.ContextResolver;
import org.drools.process.instance.impl.ReturnValueConstraintEvaluator;
import org.drools.rule.builder.PackageBuildContext;

public interface ReturnValueEvaluatorBuilder {

    public void build(final PackageBuildContext context,
                      final ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
                      final ReturnValueDescr returnValueDescr,
                      final ContextResolver contextResolver);

}