package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.bpmn2.core.Interface;
import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.Lane;
import org.drools.bpmn2.core.Message;
import org.drools.bpmn2.core.SequenceFlow;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.process.core.context.swimlane.Swimlane;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Connection;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.ConnectionRef;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.impl.NodeImpl;
import org.drools.workflow.core.node.HumanTaskNode;
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
            this.validPeers.add(Interface.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(ItemDefinition.class);
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String name = attrs.getValue("name");

		RuleFlowProcess process = new RuleFlowProcess();
		process.setAutoComplete(true);
		process.setId(id);
		if (name == null) {
		    name = id;
		}
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
        List<Lane> lanes = (List<Lane>)
            process.getMetaData(LaneHandler.LANES);
        assignLanes(process, lanes);
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
				Node source = null;
				Node target = null;
				try {
    				// remove starting _
    				sourceRef = sourceRef.substring(1);
    				targetRef = targetRef.substring(1);
    		        // remove ids of parent nodes
    				sourceRef = sourceRef.substring(sourceRef.lastIndexOf("-") + 1);
    				targetRef = targetRef.substring(targetRef.lastIndexOf("-") + 1);
    				source = nodeContainer.getNode(new Integer(sourceRef));
    				target = nodeContainer.getNode(new Integer(targetRef));
				} catch (NumberFormatException e) {
				    // try looking for a node with same "UniqueId" (in metadata)
				    for (Node node: nodeContainer.getNodes()) {
				        if (connection.getSourceRef().equals(node.getMetaData("UniqueId"))) {
				            source = node;
				            break;
				        }
				    }
                    for (Node node: nodeContainer.getNodes()) {
                        if (connection.getTargetRef().equals(node.getMetaData("UniqueId"))) {
                            target = node;
                            break;
                        }
                    }
                    if (source == null) {
                        throw new IllegalArgumentException("Could not find source node for connection:" + sourceRef);
                    }
                    if (target == null) {
                        throw new IllegalArgumentException("Could not find target node for connection:" + targetRef);
                    }
				}
				Connection result = new ConnectionImpl(
					source, NodeImpl.CONNECTION_DEFAULT_TYPE, 
					target, NodeImpl.CONNECTION_DEFAULT_TYPE);
				result.setMetaData("bendpoints", connection.getBendpoints());
				if (connection.getExpression() != null) {
					Split split = (Split) source;
					Constraint constraint = new ConstraintImpl();
					if (connection.getName() != null) {
						constraint.setName(connection.getName());
					} else {
						constraint.setName("");
					}
					if (connection.getType() != null) {
						constraint.setType(connection.getType());
					} else {
						constraint.setType("code");
					}
					if (connection.getLanguage() != null) {
						constraint.setDialect(connection.getLanguage());
					}
					constraint.setConstraint(connection.getExpression());
					split.addConstraint(
						new ConnectionRef(target.getId(), NodeImpl.CONNECTION_DEFAULT_TYPE),
						constraint);
				}
			}
		}
	}
	
	private void assignLanes(RuleFlowProcess process, List<Lane> lanes) {
	    List<String> laneNames = new ArrayList<String>();
	    Map<String, String> laneMapping = new HashMap<String, String>();
	    if (lanes != null) {
	        for (Lane lane: lanes) {
	            String name = lane.getName();
	            if (name != null) {
	                Swimlane swimlane = new Swimlane();
	                swimlane.setName(name);
	                process.getSwimlaneContext().addSwimlane(swimlane);
	                laneNames.add(name);
	                for (String flowElementRef: lane.getFlowElements()) {
	                    laneMapping.put(flowElementRef, name);
	                }
	            }
	        }
	    }
	    assignLanes(process, laneMapping);
	}
	
	private void assignLanes(NodeContainer nodeContainer, Map<String, String> laneMapping) {
	    for (Node node: nodeContainer.getNodes()) {
	        String lane = null;
	        String uniqueId = (String) node.getMetaData("UniqueId");
	        if (uniqueId != null) {
	            lane = laneMapping.get(uniqueId);
	        } else {
	            lane = laneMapping.get(((NodeImpl) node).getUniqueId());
	        }
	        if (lane != null) {
	            ((NodeImpl) node).setMetaData("Lane", lane);
	            if (node instanceof HumanTaskNode) {
	                ((HumanTaskNode) node).setSwimlane(lane);
	            }
	        }
	        if (node instanceof NodeContainer) {
	            assignLanes((NodeContainer) node, laneMapping);
	        }
	    }
	}

}