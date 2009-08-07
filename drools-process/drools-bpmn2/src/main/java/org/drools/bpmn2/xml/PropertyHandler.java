package org.drools.bpmn2.xml;

import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.WorkflowProcess;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PropertyHandler extends BaseAbstractHandler implements Handler {

	public PropertyHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(RuleFlowProcess.class);
        this.validParents.add(WorkItemNode.class);
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

		final String id = attrs.getValue("id");
		final String itemSubjectRef = attrs.getValue("itemSubjectRef");

		Object parent = parser.getParent();
		if (parent instanceof RuleFlowProcess) {
			RuleFlowProcess process = (RuleFlowProcess) parent;
			List variables = process.getVariableScope().getVariables();
			Variable variable = new Variable();
			variable.setName(id);
			// TODO add object type
			variable.setType(new ObjectDataType());
			variables.add(variable);
			return variable;
		}

		return new Variable();
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Variable.class;
	}

}
