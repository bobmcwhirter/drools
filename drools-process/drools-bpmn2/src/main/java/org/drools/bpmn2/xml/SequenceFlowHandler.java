package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.process.core.context.variable.Variable;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SequenceFlowHandler extends BaseAbstractHandler implements Handler {

	public SequenceFlowHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(NodeContainer.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(Variable.class);
        this.validPeers.add(Node.class);
        this.validPeers.add(SequenceFlow.class);
    }
    
	@SuppressWarnings("unchecked")
	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		final String sourceRef = attrs.getValue("sourceRef");
		final String targetRef = attrs.getValue("targetRef");
		final String bendpoints = attrs.getValue("g:bendpoints");

		RuleFlowProcess process = (RuleFlowProcess) parser.getParent();
		
		List<SequenceFlow> connections = (List<SequenceFlow>)
			process.getMetaData(ProcessHandler.CONNECTIONS);
		if (connections == null) {
			connections = new ArrayList<SequenceFlow>();
			process.setMetaData(ProcessHandler.CONNECTIONS, connections);
		}
		SequenceFlow connection = new SequenceFlow(sourceRef, targetRef);
		connection.setBendpoints(bendpoints);
		
		connections.add(connection);

		return connection;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		final Element element = parser.endElementBuilder();
		SequenceFlow sequenceFlow = (SequenceFlow) parser.getCurrent();
        
		org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode != null) {
        	String nodeName = xmlNode.getNodeName();
        	if ("conditionExpression".equals(nodeName)) {
        		String expression = xmlNode.getTextContent();
        		sequenceFlow.setExpression(expression);
        	}
        	xmlNode = xmlNode.getNextSibling();
        }
        return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return SequenceFlow.class;
	}

}
