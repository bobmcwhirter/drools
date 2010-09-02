/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.bpmn2.xpath;

import java.util.List;
import java.util.Map;

import org.drools.base.TypeResolver;
import org.drools.compiler.Dialect.AnalysisResult;
import org.drools.io.Resource;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ProcessDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.process.builder.ActionBuilder;
import org.drools.process.builder.ProcessBuildContext;
import org.drools.process.builder.ProcessClassBuilder;
import org.drools.process.builder.ReturnValueEvaluatorBuilder;
import org.drools.process.builder.dialect.ProcessDialect;
import org.drools.rule.builder.AccumulateBuilder;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.EnabledBuilder;
import org.drools.rule.builder.EngineElementBuilder;
import org.drools.rule.builder.EntryPointBuilder;
import org.drools.rule.builder.FromBuilder;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.PatternBuilder;
import org.drools.rule.builder.PredicateBuilder;
import org.drools.rule.builder.QueryBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.rule.builder.RuleClassBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.SalienceBuilder;

public class XPathDialect implements ProcessDialect {

    public static final String ID = "XPath";
    
	private static final XPathReturnValueEvaluatorBuilder RETURN_VALUE_EVALUATOR_BUILDER = new XPathReturnValueEvaluatorBuilder();
	
	public String getId() {
		return ID;
	}

	public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
		return RETURN_VALUE_EVALUATOR_BUILDER;
	}

	public void addProcess(ProcessBuildContext context) {
		
	}

	public ActionBuilder getActionBuilder() {
		throw new UnsupportedOperationException("XPath does not support actoins");
	}

	public ProcessClassBuilder getProcessClassBuilder() {
		throw new UnsupportedOperationException("XPath does not support class builder");
	}

}
