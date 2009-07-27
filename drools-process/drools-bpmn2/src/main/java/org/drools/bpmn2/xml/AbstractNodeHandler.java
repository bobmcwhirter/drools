package org.drools.bpmn2.xml;

import java.util.HashSet;

import org.drools.bpmn2.core.SequenceFlow;
import org.drools.process.core.context.variable.Variable;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractNodeHandler extends BaseAbstractHandler implements Handler {

    protected final static String EOL = System.getProperty( "line.separator" );

    public AbstractNodeHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(NodeContainer.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(Variable.class);
        this.validPeers.add(Node.class);
        this.validPeers.add(SequenceFlow.class);
    }

    public Object start(final String uri, final String localName, final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName, attrs );
        NodeContainer nodeContainer = (NodeContainer) parser.getParent();
        final Node node = createNode(attrs);
        final String id = attrs.getValue("id");
        node.setName(id);
        node.setId(nodeContainer.getNodes().length);
        nodeContainer.addNode(node);
        return node;
    }

    protected abstract Node createNode(Attributes attrs);

    public Object end(final String uri, final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        final Element element = parser.endElementBuilder();
        Node node = (Node) parser.getCurrent();
        handleNode(node, element, uri, localName, parser);
        return node;
    }
    
    protected void handleNode(final Node node, final Element element, final String uri, 
                              final String localName, final ExtensibleXmlParser parser)
    	throws SAXException {
    }
    
    public abstract void writeNode(final Node node, final StringBuilder xmlDump,
    		                       final boolean includeMeta);
    
    protected void writeNode(final String name, final Node node, 
    		                 final StringBuilder xmlDump) {
    	xmlDump.append("    <" + name + " "); 
        if (node.getName() != null) {
            xmlDump.append("name=\"" + node.getName() + "\" ");
        }
    }
    
    protected void endNode(final StringBuilder xmlDump) {
        xmlDump.append("/>" + EOL);
    }

    protected void endNode(final String name, final StringBuilder xmlDump) {
        xmlDump.append("    </" + name + ">" + EOL);
    }
    
}
