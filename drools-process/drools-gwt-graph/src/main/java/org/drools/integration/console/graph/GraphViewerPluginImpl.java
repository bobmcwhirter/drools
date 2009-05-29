package org.drools.integration.console.graph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.definition.process.Node;
import org.drools.definition.process.NodeContainer;
import org.drools.definition.process.Process;
import org.drools.definition.process.WorkflowProcess;
import org.drools.io.ResourceFactory;
import org.drools.process.audit.NodeInstanceLog;
import org.drools.process.audit.ProcessInstanceDbLog;
import org.drools.process.audit.ProcessInstanceLog;
import org.jboss.bpm.console.client.model.ActiveNodeInfo;
import org.jboss.bpm.console.client.model.DiagramInfo;
import org.jboss.bpm.console.client.model.DiagramNodeInfo;
import org.jboss.bpm.console.server.plugin.GraphViewerPlugin;

/**
 * @author Kris Verlaenen
 */
public class GraphViewerPluginImpl implements GraphViewerPlugin {

	public ActiveNodeInfo getActiveNodeInfo(String instanceId) {
		// TODO: there can be more than one active node !
		ProcessInstanceLog processInstance = ProcessInstanceDbLog.findProcessInstance(new Long(instanceId));
		if (processInstance == null) {
			throw new IllegalArgumentException("Could not find process instance " + instanceId);
		}
		Map<String, NodeInstanceLog> nodeInstances = new HashMap<String, NodeInstanceLog>();
		for (NodeInstanceLog nodeInstance: ProcessInstanceDbLog.findNodeInstances(new Long(instanceId))) {
			if (nodeInstance.getType() == NodeInstanceLog.TYPE_ENTER) {
				nodeInstances.put(nodeInstance.getNodeInstanceId(), nodeInstance);
			} else {
				nodeInstances.remove(nodeInstance.getNodeInstanceId());
			}
		}
		if (!nodeInstances.isEmpty()) {
			NodeInstanceLog nodeInstance = nodeInstances.values().iterator().next();
			DiagramInfo diagramInfo = getDiagramInfo(processInstance.getProcessId());
			for (DiagramNodeInfo nodeInfo: diagramInfo.getNodeList()) {
				if (nodeInfo.getName().equals("id=" + nodeInstance.getNodeId())) {
					return new ActiveNodeInfo(diagramInfo.getWidth(), diagramInfo.getHeight(), nodeInfo);
				}
			}
			throw new IllegalArgumentException("Could not find info for node "
				+ nodeInstance.getNodeId() + " of process " + processInstance.getProcessId());
		}
		return null;
	}

	public DiagramInfo getDiagramInfo(String processId) {
		KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("Guvnor default");
		kagent.applyChangeSet(ResourceFactory.newClassPathResource("ChangeSet.xml"));
		kagent.monitorResourceChangeEvents(false);
		KnowledgeBase kbase = kagent.getKnowledgeBase();
		Process process = kbase.getProcess(processId);
		if (process == null) {
			return null;
		}

		DiagramInfo result = new DiagramInfo();
		result.setWidth(932);
		result.setHeight(541);
		List<DiagramNodeInfo> nodeList = new ArrayList<DiagramNodeInfo>();
		if (process instanceof WorkflowProcess) {
			addNodesInfo(nodeList, ((WorkflowProcess) process).getNodes(), "id=");
		}
		result.setNodeList(nodeList);
		return result;
	}
	
	private void addNodesInfo(List<DiagramNodeInfo> nodeInfos, Node[] nodes, String prefix) {
		for (Node node: nodes) {
			nodeInfos.add(new DiagramNodeInfo(
				prefix + node.getId(),
				(Integer) node.getMetaData("x"),
				(Integer) node.getMetaData("y"),
				(Integer) node.getMetaData("width"),
				(Integer) node.getMetaData("height")));
			if (node instanceof NodeContainer) {
				addNodesInfo(nodeInfos, ((NodeContainer) node).getNodes(), prefix + node.getId() + ":");
			}
		}
	}

	public byte[] getProcessImage(String processId) {
		InputStream is = GraphViewerPluginImpl.class.getResourceAsStream("/" + processId + ".png");
		if (is != null) {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			try {
				transfer(is, os);
			} catch (IOException e) {
				throw new RuntimeException("Could not read process image: " + e.getMessage());
			}
			return os.toByteArray();
		}
		return null;
	}
	
	private static final int BUFFER_SIZE = 512;

	public static int transfer(InputStream in, OutputStream out) throws IOException {
		int total = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		int bytesRead = in.read(buffer);
		while (bytesRead != -1) {
			out.write(buffer, 0, bytesRead);
			total += bytesRead;
			bytesRead = in.read(buffer);
		}
		return total;
	}

}
