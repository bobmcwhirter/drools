package org.drools.bpmn2.xml.di;

import java.util.HashSet;

import org.drools.bpmn2.xml.di.ProcessDiagramHandler.ProcessInfo;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class ShapeHandler extends BaseAbstractHandler implements Handler {

    public ShapeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = true;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(ProcessInfo.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(NodeInfo.class);
    }
    
    public Object start(final String uri, final String localName,
                        final Attributes attrs, final ExtensibleXmlParser parser)
            throws SAXException {
        parser.startElementBuilder(localName, attrs);
        final String id = attrs.getValue("id");
        final String eventRef = attrs.getValue(getNodeRefName());
        final String height = attrs.getValue("height");
        final String width = attrs.getValue("width");
        final String x = attrs.getValue("x");
        final String y = attrs.getValue("y");
        NodeInfo nodeInfo = new NodeInfo(id, eventRef);
        nodeInfo.setX(new Float(x).intValue());
        nodeInfo.setY(new Float(y).intValue());
        nodeInfo.setWidth(new Float(width).intValue());
        nodeInfo.setHeight(new Float(height).intValue());
        ProcessInfo processInfo = (ProcessInfo) parser.getParent();
        processInfo.addNodeInfo(nodeInfo);
        return nodeInfo;
    }
    
    protected abstract String getNodeRefName();

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        parser.endElementBuilder();
        return parser.getCurrent();
    }

    public Class<?> generateNodeFor() {
        return NodeInfo.class;
    }
    
    public static class NodeInfo {
        
        private String id;
        private String nodeRef;
        private Integer x;
        private Integer y;
        private Integer width;
        private Integer height;

        public NodeInfo(String id, String nodeRef) {
            this.id = id;
            this.nodeRef = nodeRef;
        }
        
        public String getId() {
            return id;
        }

        public String getNodeRef() {
            return nodeRef;
        }
        
        public Integer getX() {
            return x;
        }

        public void setX(Integer x) {
            this.x = x;
        }

        public Integer getY() {
            return y;
        }

        public void setY(Integer y) {
            this.y = y;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }
        
    }

}