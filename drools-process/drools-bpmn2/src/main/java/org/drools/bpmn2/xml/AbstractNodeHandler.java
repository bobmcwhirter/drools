package org.drools.bpmn2.xml;

import java.util.HashSet;

import org.drools.bpmn2.core.Lane;
import org.drools.bpmn2.core.SequenceFlow;
import org.drools.process.core.context.variable.Variable;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.XmlDumper;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class AbstractNodeHandler extends BaseAbstractHandler implements Handler {

    protected final static String EOL = System.getProperty( "line.separator" );

    public AbstractNodeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = true;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(NodeContainer.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(Lane.class);
        this.validPeers.add(Variable.class);
        this.validPeers.add(Node.class);
        this.validPeers.add(SequenceFlow.class);
    }

    public Object start(final String uri, final String localName, final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        final Node node = createNode(attrs);
        String id = attrs.getValue("id");
        try {
            // remove starting _
            id = id.substring(1);
            // remove ids of parent nodes
            id = id.substring(id.lastIndexOf("-") + 1);
            final String name = attrs.getValue("name");
            node.setName(name);
            node.setId(new Integer(id));
        } catch (NumberFormatException e) {
            // id is not in the expected format, generating a new one
            long newId = 0;
            NodeContainer nodeContainer = (NodeContainer) parser.getParent();
            for (org.drools.definition.process.Node n: nodeContainer.getNodes()) {
                if (n.getId() > newId) {
                    newId = n.getId();
                }
            }
            ((org.drools.workflow.core.Node) node).setId(++newId);
            node.setMetaData("UniqueId", attrs.getValue("id"));
        }
        return node;
    }

    protected abstract Node createNode(Attributes attrs);

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        handleNode(node, element, uri, localName, parser);
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        nodeContainer.addNode(node);
        return node;
    }
    
    protected void handleNode(final Node node, final Element element, final String uri, 
                              final String localName, final ExtensibleXmlParser parser)
    	throws SAXException {
        final String x = element.getAttribute("x");
        if (x != null && x.length() != 0) {
            try {
                node.setMetaData("x", new Integer(x));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'x' attribute", parser.getLocator());
            }
        }
        final String y = element.getAttribute("y");
        if (y != null && y.length() != 0) {
            try {
                node.setMetaData("y", new Integer(y));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'y' attribute", parser.getLocator());
            }
        }
        final String width = element.getAttribute("width");
        if (width != null && width.length() != 0) {
            try {
                node.setMetaData("width", new Integer(width));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'width' attribute", parser.getLocator());
            }
        }
        final String height = element.getAttribute("height");
        if (height != null && height.length() != 0) {
            try {
                node.setMetaData("height", new Integer(height));
            } catch (NumberFormatException exc) {
                throw new SAXParseException("<" + localName + "> requires an Integer 'height' attribute", parser.getLocator());
            }
        }
    }
    
    public abstract void writeNode(final Node node, final StringBuilder xmlDump,
    		                       final boolean includeMeta);
    
    protected void writeNode(final String name, final Node node, 
    		                 final StringBuilder xmlDump, boolean includeMeta) {
    	xmlDump.append("    <" + name + " "); 
        xmlDump.append("id=\"_" + XmlBPMNProcessDumper.getUniqueNodeId(node) + "\" ");
        if (node.getName() != null) {
            xmlDump.append("name=\"" + XmlDumper.replaceIllegalChars(node.getName()) + "\" ");
        }
        if (includeMeta) {
            Integer x = (Integer) node.getMetaData("x");
            Integer y = (Integer) node.getMetaData("y");
            Integer width = (Integer) node.getMetaData("width");
            Integer height = (Integer) node.getMetaData("height");
            if (x != null && x != 0) {
                xmlDump.append("g:x=\"" + x + "\" ");
            }
            if (y != null && y != 0) {
                xmlDump.append("g:y=\"" + y + "\" ");
            }
            if (width != null && width != -1) {
                xmlDump.append("g:width=\"" + width + "\" ");
            }
            if (height != null && height != -1) {
                xmlDump.append("g:height=\"" + height + "\" ");
            }
        }
    }
    
    protected void endNode(final StringBuilder xmlDump) {
        xmlDump.append("/>" + EOL);
    }

    protected void endNode(final String name, final StringBuilder xmlDump) {
        xmlDump.append("    </" + name + ">" + EOL);
    }
    
}
