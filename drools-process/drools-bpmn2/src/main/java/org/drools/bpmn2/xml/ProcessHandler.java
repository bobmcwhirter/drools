package org.drools.bpmn2.xml;

import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.Split;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ProcessHandler extends BaseAbstractHandler implements Handler {
	
	public static final String CONNECTIONS = "BPMN.Connections";

	@SuppressWarnings("unchecked")
	public ProcessHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(null);

			this.validPeers = new HashSet();
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		final String id = attrs.getValue("id");
		final String name = attrs.getValue("name");

		RuleFlowProcess process = new RuleFlowProcess();
		process.setId(id);
		process.setName(name);
		process.setType("RuleFlow");
		process.setPackageName("org.drools.bpmn2");

		((ProcessBuildData) parser.getData()).setProcess(process);
		return process;
	}

	@SuppressWarnings("unchecked")
	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		RuleFlowProcess process = (RuleFlowProcess) parser.getCurrent();
		List<SequenceFlow> connections = (List<SequenceFlow>)
			process.getMetaData(CONNECTIONS);
		linkConnections(process, connections);
		return process;
	}

	public Class<?> generateNodeFor() {
		return org.drools.definition.process.Process.class;
	}
	
	public static void linkConnections(NodeContainer nodeContainer, List<SequenceFlow> connections) {
		if (connections != null) {
			for (SequenceFlow connection: connections) {
				String sourceRef = connection.getSourceRef();
				String targetRef = connection.getTargetRef();
				// remove starting _
				sourceRef = sourceRef.substring(1);
				targetRef = targetRef.substring(1);
		        // remove ids of parent nodes
				sourceRef = sourceRef.substring(sourceRef.lastIndexOf("-") + 1);
				targetRef = targetRef.substring(targetRef.lastIndexOf("-") + 1);
				Node source = nodeContainer.getNode(new Integer(sourceRef));
				Node target = nodeContainer.getNode(new Integer(targetRef));
				Connection result = new ConnectionImpl(
					source, NodeImpl.CONNECTION_DEFAULT_TYPE, 
					target, NodeImpl.CONNECTION_DEFAULT_TYPE);
				result.setMetaData("bendpoints", connection.getBendpoints());
				if (connection.getExpression() != null) {
					Split split = (Split) source;
					Constraint constraint = new ConstraintImpl();
					constraint.setType("code");
					constraint.setName("");
					constraint.setConstraint(connection.getExpression());
					split.addConstraint(
						new ConnectionRef(target.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE),
						constraint);
				}
			}
		}
	}

}