package org.drools.bpmn2.xml.di;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.Definitions;
import org.drools.bpmn2.xml.di.BPMNEdgeHandler.ConnectionInfo;
import org.drools.bpmn2.xml.di.BPMNShapeHandler.NodeInfo;
import org.drools.compiler.xml.ProcessBuildData;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BPMNPlaneHandler extends BaseAbstractHandler implements Handler {

    public BPMNPlaneHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(Definitions.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
		this.validPeers.add(null);
        this.validPeers.add(org.drools.definition.process.Process.class);
    }
    
    public Object start(final String uri, final String localName,
                        final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);

        final String processRef = attrs.getValue("bpmnElement");
        ProcessInfo info = new ProcessInfo(processRef);
        return info;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        ProcessInfo processInfo = (ProcessInfo) parser.getCurrent();
        RuleFlowProcess process = (RuleFlowProcess)
            ((ProcessBuildData) parser.getData()).getProcess();
        for (NodeInfo nodeInfo: processInfo.getNodeInfos()) {
        	boolean found = processNodeInfo(nodeInfo, process.getNodes());
            if (!found) {
                throw new IllegalArgumentException(
                    "Could not find node " + nodeInfo.getNodeRef());
            }
        }
        postProcessNodeOffset(process.getNodes(), 0, 0);
        for (ConnectionInfo connectionInfo: processInfo.getConnectionInfos()) {
            if (connectionInfo.getBendpoints() != null) {
            	boolean found = processConnectionInfo(connectionInfo, process.getNodes());
                if (!found) {
                    throw new IllegalArgumentException(
                        "Could not find connection " + connectionInfo.getElementRef());
                }
            }
        }
        return processInfo;
    }
    
    private boolean processNodeInfo(NodeInfo nodeInfo, Node[] nodes) {
        for (Node node: nodes) {
            String id = (String) node.getMetaData("UniqueId");
            if (nodeInfo.getNodeRef().equals(id)) {
                ((org.drools.workflow.core.Node) node).setMetaData("x", nodeInfo.getX());
                ((org.drools.workflow.core.Node) node).setMetaData("y", nodeInfo.getY());
                ((org.drools.workflow.core.Node) node).setMetaData("width", nodeInfo.getWidth());
                ((org.drools.workflow.core.Node) node).setMetaData("height", nodeInfo.getHeight());
                return true;
            }
        	if (node instanceof NodeContainer) {
        		boolean found = processNodeInfo(nodeInfo, ((NodeContainer) node).getNodes());
        		if (found) {
        			return true;
        		}
            }
        }
        return false;
    }
    
    private void postProcessNodeOffset(Node[] nodes, int xOffset, int yOffset) {
    	for (Node node: nodes) {
    		Integer x = (Integer) node.getMetaData("x");
    		if (x != null) {
    			((org.drools.workflow.core.Node) node).setMetaData("x", x - xOffset);
    		}
    		Integer y = (Integer) node.getMetaData("y");
    		if (y != null) {
    			((org.drools.workflow.core.Node) node).setMetaData("y", y - yOffset);
    		}
    		if (node instanceof NodeContainer) {
    			postProcessNodeOffset(((NodeContainer) node).getNodes(), xOffset + (x == null ? 0 : x), yOffset + (y == null ? 0 : y));
    		}
    	}
    }
    
    private boolean processConnectionInfo(ConnectionInfo connectionInfo, Node[] nodes) {
        for (Node node: nodes) {
        	for (List<Connection> connections: node.getOutgoingConnections().values()) {
        		for (Connection connection: connections) {
                    String id = (String) connection.getMetaData("UniqueId");
                    if (id != null && id.equals(connectionInfo.getElementRef())) {
                        ((ConnectionImpl) connection).setMetaData(
                            "bendpoints", connectionInfo.getBendpoints());
                        return true;
                    }
        		}
        	}
        	if (node instanceof NodeContainer) {
        		boolean found = processConnectionInfo(connectionInfo, ((NodeContainer) node).getNodes());
        		if (found) {
        			return true;
        		}
        	}
		}
        return false;
    }

    public Class<?> generateNodeFor() {
        return ProcessInfo.class;
    }
    
    public static class ProcessInfo {
        
        private String processRef;
        private List<NodeInfo> nodeInfos = new ArrayList<NodeInfo>();
        private List<ConnectionInfo> connectionInfos = new ArrayList<ConnectionInfo>();

        public ProcessInfo(String processRef) {
            this.processRef = processRef;
        }
        
        public String getProcessRef() {
            return processRef;
        }
        
        public void addNodeInfo(NodeInfo nodeInfo) {
            this.nodeInfos.add(nodeInfo);
        }
        
        public List<NodeInfo> getNodeInfos() {
            return nodeInfos;
        }
        
        public void addConnectionInfo(ConnectionInfo connectionInfo) {
            connectionInfos.add(connectionInfo);
        }
        
        public List<ConnectionInfo> getConnectionInfos() {
            return connectionInfos;
        }
        
    }

}