package org.drools.bpel.xpath;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.xpath.XPathEvaluator;

import org.drools.compiler.ReturnValueDescr;
import org.drools.rule.builder.PackageBuildContext;
import org.drools.rule.builder.ReturnValueEvaluatorBuilder;
import org.drools.workflow.instance.impl.ReturnValueConstraintEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XPathReturnValueEvaluatorBuilder implements ReturnValueEvaluatorBuilder {

	public void build(PackageBuildContext context,
			ReturnValueConstraintEvaluator returnValueConstraintEvaluator,
			ReturnValueDescr returnValueDescr) {
		try {
			XPathReturnValueEvaluator evaluator = new XPathReturnValueEvaluator();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			Document document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(returnValueDescr.getText().getBytes()));
			Node node = document.getFirstChild().getFirstChild();
	        if (node == null) {
	            throw new IllegalStateException();
	        }
	        if (node.getNodeType() != Node.TEXT_NODE) {
	            throw new IllegalArgumentException("Unexpected node type for XPath");
	        }
	        String xpathString = node.getNodeValue();
			XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
//	        xpf.setXPathFunctionResolver(new XPathFunctionResolver() {
//				public XPathFunction resolveFunction(QName arg0, int arg1) {
//					return null;
//				}
//	        });
//	        xpf.setXPathVariableResolver(new XPathVariableResolver() {
//	        	public Object resolveVariable(QName arg0) {
//					return null;
//				}
//	        });
	        XPathEvaluator xpe = (XPathEvaluator) xpf.newXPath();
//	        xpe.setNamespaceContext(new NamespaceContext() {
//				public String getNamespaceURI(String arg0) {
//					return null;
//				}
//				public String getPrefix(String arg0) {
//					return null;
//				}
//				public Iterator getPrefixes(String arg0) {
//					return null;
//				}
//	        });
	        XPathExpression expression = xpe.compile(xpathString);
	        evaluator.setExpression(expression);
			returnValueConstraintEvaluator.setEvaluator(evaluator);
		} catch (Throwable t) {
			throw new IllegalArgumentException("Could not compile XPath expression " + returnValueDescr.getText(), t);
		}
	}

}
