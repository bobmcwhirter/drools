package org.drools.bpmn2.xml.di;

import java.util.HashSet;

import org.drools.bpmn2.xml.di.ProcessDiagramHandler.ProcessInfo;
import org.drools.bpmn2.xml.di.ShapeHandler.NodeInfo;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SequenceFlowConnectorHandler extends BaseAbstractHandler implements Handler {

    public SequenceFlowConnectorHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(ProcessInfo.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(NodeInfo.class);
        this.validPeers.add(ConnectionInfo.class);
    }
    
    public Object start(final String uri, final String localName,
                        final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);

        final String sequenceFlowRef = attrs.getValue("sequenceFlowRef");
        ConnectionInfo info = new ConnectionInfo(sequenceFlowRef);
        final String sourceRef = attrs.getValue("sourceRef");
        final String targetRef = attrs.getValue("targetRef");
        info.setSourceRef(sourceRef);
        info.setTargetRef(targetRef);
        ProcessInfo processInfo = (ProcessInfo) parser.getParent();
        processInfo.addConnectionInfo(info);
        return info;
    }

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        Element element = parser.endElementBuilder();
        // now get bendpoints
        String bendpoints = null;
        org.w3c.dom.Node xmlNode = element.getFirstChild();
        while (xmlNode instanceof Element) {
            String nodeName = xmlNode.getNodeName();
            if ("bendpoint".equals(nodeName)) {
                String x = ((Element) xmlNode).getAttribute("x");
                String y = ((Element) xmlNode).getAttribute("y");
                try {
                    int xValue = new Float(x).intValue();
                    int yValue = new Float(y).intValue();
                    if (bendpoints == null) {
                        bendpoints = "[";
                    } else {
                        bendpoints += ";";
                    }
                    bendpoints += xValue + "," + yValue;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid bendpoint value", e);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
        ConnectionInfo connectionInfo = (ConnectionInfo) parser.getCurrent();
        if (bendpoints != null) {
            connectionInfo.setBendpoints(bendpoints + "]");
        }
        return connectionInfo;
    }

    public Class<?> generateNodeFor() {
        return ConnectionInfo.class;
    }
    
    public static class ConnectionInfo {
        
        private String sequenceFlowRef;
        private String sourceRef;
        private String targetRef;
        private String bendpoints;

        public ConnectionInfo(String sequenceFlowRef) {
            this.sequenceFlowRef = sequenceFlowRef;
        }
        
        public String getSequenceFlowRef() {
            return sequenceFlowRef;
        }

        public String getSourceRef() {
            return sourceRef;
        }

        public void setSourceRef(String sourceRef) {
            this.sourceRef = sourceRef;
        }

        public String getTargetRef() {
            return targetRef;
        }

        public void setTargetRef(String targetRef) {
            this.targetRef = targetRef;
        }

        public String getBendpoints() {
            return bendpoints;
        }

        public void setBendpoints(String bendpoints) {
            this.bendpoints = bendpoints;
        }
        
    }

}