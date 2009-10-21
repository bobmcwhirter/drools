package org.drools.bpmn2.xpath;

import org.drools.compiler.ReturnValueDescr;
import org.drools.process.core.ContextResolver;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;

public class XPathReturnValueEvaluatorBuilder implements ReturnValueEvaluatorBuilder {

    public void build(PackageBuildContext context,
            ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
            ReturnValueDescr returnValueDescr, ContextResolver contextResolver) {
        XPathReturnValueEvaluator evaluator = new XPathReturnValueEvaluator();
        String xpathString = returnValueDescr.getText();
        evaluator.setExpression(xpathString);
        returnValueConstraintEvaluator.setEvaluator(evaluator);
    }

}
