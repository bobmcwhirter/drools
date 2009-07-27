package org.drools.bpmn2.xml;

import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.Node;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.NodeImpl;
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

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		RuleFlowProcess process = (RuleFlowProcess) parser.getCurrent();
		linkConnections(process);
		return process;
	}

	public Class<?> generateNodeFor() {
		return org.drools.definition.process.Process.class;
	}
	
	@SuppressWarnings("unchecked")
	private void linkConnections(RuleFlowProcess process) {
		List<SequenceFlow> connections = (List<SequenceFlow>) process.getMetaData(CONNECTIONS);
		if (connections != null) {
			for (SequenceFlow connection: connections) {
				String sourceRef = connection.getSourceRef();
				String targetRef = connection.getTargetRef();
				Node source = null;
				for (Node node: process.getNodes()) {
					if (sourceRef.equals(node.getName())) {
						source = node;
						break;
					}
				}
				Node target = null;
				for (Node node: process.getNodes()) {
					if (targetRef.equals(node.getName())) {
						target = node;
						break;
					}
				}
				if (source == null) {
					throw new IllegalArgumentException(
						"Could not find source " + sourceRef);
				}
				if (target == null) {
					throw new IllegalArgumentException(
						"Could not find target " + targetRef);
				}
				new ConnectionImpl(
					source, NodeImpl.CONNECTION_DEFAULT_TYPE, 
					target, NodeImpl.CONNECTION_DEFAULT_TYPE);
			}
		}
	}

}