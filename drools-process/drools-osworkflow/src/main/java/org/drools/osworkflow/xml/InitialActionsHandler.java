package org.drools.osworkflow.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.definition.process.Process;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.core.node.StepNode;
import org.drools.osworkflow.instance.OSWorkflowProcessInstance;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.processes.AbstractNodeHandler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;

public class InitialActionsHandler extends BaseAbstractHandler implements
			Handler {
	OSWorkflowProcess  process = null;
	
	public InitialActionsHandler() {
		if ( (this.validParents == null) && (this.validPeers == null) ) {
            this.validParents = new HashSet();
            this.validParents.add( ContextContainer.class );

            this.validPeers = new HashSet();         
            this.validPeers.add( null );            

            this.allowNesting = false;
        }
    }
    

    
    public Object start(final String uri,
                        final String localName,
                        final Attributes attrs,
                        final ExtensibleXmlParser parser) throws SAXException {
        parser.startElementBuilder( localName,
                                    attrs );
       
        
        
        process = ( OSWorkflowProcess ) parser.getParent();        
        
        
        
        
        
        return null;
    }    
    
    public Object end(final String uri,
                      final String localName,
                      final ExtensibleXmlParser parser) throws SAXException {
        
    	Element element = parser.endElementBuilder();
    	
    	
        
        
        List<ActionDescriptor> actionList = new ArrayList<ActionDescriptor>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            org.w3c.dom.Node subNode = nodeList.item(i);
            if (subNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                Element subElement = (Element) subNode;
                ActionDescriptor actionDescriptor = 
                    DescriptorFactory.getFactory().createActionDescriptor(subElement);
                actionList.add(actionDescriptor);
            }
        }
        
        process.setInitialActions(actionList);
    	
    	
    	
    	
    	
        return null;
    }

    public Class<?> generateNodeFor() {
        return Variable.class;
    }    

}
