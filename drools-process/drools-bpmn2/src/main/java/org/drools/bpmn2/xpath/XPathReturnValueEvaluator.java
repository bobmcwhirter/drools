package org.drools.bpmn2.xpath;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import org.drools.WorkingMemory;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.spi.ProcessContext;
import org.drools.spi.ReturnValueEvaluator;

public class XPathReturnValueEvaluator implements ReturnValueEvaluator {
    
    private static XPathScriptEngineFactory FACTORY = new XPathScriptEngineFactory();

    private String expression;
    
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Object evaluate(WorkingMemory workingMemory, ProcessContext processContext) throws Exception {
        ScriptEngine engine = FACTORY.getScriptEngine();
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
            ((ProcessInstance) processContext.getProcessInstance())
                .getContextInstance(VariableScope.VARIABLE_SCOPE);
        if (variableScopeInstance != null) {
            // TODO include other scopes that process-level scope as well
            Bindings bindings = new SimpleBindings(variableScopeInstance.getVariables());
            return engine.eval(expression, bindings);
        } else {
            return engine.eval(expression);
        }
    }

}
