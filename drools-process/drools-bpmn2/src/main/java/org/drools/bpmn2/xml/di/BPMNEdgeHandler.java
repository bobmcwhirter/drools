package org.drools.bpmn2.xml.di;

import java.util.HashSet;

import org.drools.bpmn2.xml.di.BPMNPlaneHandler.ProcessInfo;
import org.drools.bpmn2.xml.di.BPMNShapeHandler.NodeInfo;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class BPMNEdgeHandler extends BaseAbstractHandler implements Handler {

    public BPMNEdgeHandler() {
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

        final String elementRef = attrs.getValue("bpmnElement");
        ConnectionInfo info = new ConnectionInfo(elementRef);
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
            if ("waypoint".equals(nodeName)) {
                // ignore first and last waypoint
                String x = ((Element) xmlNode).getAttribute("x");
                String y = ((Element) xmlNode).getAttribute("y");
                try {
                    int xValue = new Float(x).intValue();
                    int yValue = new Float(y).intValue();
                    if (bendpoints == null) {
                        bendpoints = "[";
                    } else if (xmlNode.getNextSibling() != null) {
                        bendpoints += xValue + "," + yValue;
                        bendpoints += ";";
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid bendpoint value", e);
                }
            }
            xmlNode = xmlNode.getNextSibling();
        }
        ConnectionInfo connectionInfo = (ConnectionInfo) parser.getCurrent();
        if (bendpoints != null && bendpoints.length() > 1) {
            connectionInfo.setBendpoints(bendpoints + "]");
        }
        return connectionInfo;
    }

    public Class<?> generateNodeFor() {
        return ConnectionInfo.class;
    }
    
    public static class ConnectionInfo {
        
        private String elementRef;
        private String bendpoints;

        public ConnectionInfo(String elementRef) {
            this.elementRef = elementRef;
        }
        
        public String getElementRef() {
            return elementRef;
        }

        public String getBendpoints() {
            return bendpoints;
        }

        public void setBendpoints(String bendpoints) {
            this.bendpoints = bendpoints;
        }
        
    }

}