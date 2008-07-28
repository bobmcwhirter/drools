package org.drools.bpel.xpath;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;

import org.apache.ode.utils.DOMUtils;
import org.drools.WorkingMemory;
import org.drools.spi.ReturnValueEvaluator;

public class XPathReturnValueEvaluator implements ReturnValueEvaluator {
	
	private XPathExpression expression;
	
	public void setExpression(XPathExpression expression) {
		this.expression = expression;
	}

	public Object evaluate(WorkingMemory workingMemory) throws Exception {
		return expression.evaluate(DOMUtils.newDocument(), XPathConstants.BOOLEAN);
	}

}
