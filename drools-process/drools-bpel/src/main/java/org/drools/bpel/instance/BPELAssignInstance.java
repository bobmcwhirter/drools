package org.drools.bpel.instance;

import java.io.ByteArrayInputStream;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.ode.utils.DOMUtils;
import org.drools.bpel.core.BPELAssign;
import org.drools.bpel.core.BPELAssign.Copy;
import org.drools.bpel.core.BPELAssign.VariablePart;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.workflow.core.Node;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.impl.NodeInstanceImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELAssignInstance extends NodeInstanceImpl {
    
    private static final long serialVersionUID = 400L;

    public BPELAssign getBPELAssign() {
    	return (BPELAssign) getNode();
    }
    
    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
        	BPELAssign assign = getBPELAssign();
        	for (Copy copy: assign.getCopies()) {
        		VariablePart fromPart = copy.getFrom();
        		VariablePart toPart = copy.getTo();
        		String fromValue = getVariableValue(fromPart.getVariable());
        		String toValue = getVariableValue(toPart.getVariable());
        		if (toValue == null) {
        			toValue = initializeVariable(toPart.getVariable());
        		}
        		toValue = copy(fromValue, fromPart.getPart(), toValue, toPart.getPart());
        		setVariableValue(toPart.getVariable(), toValue);
        	}
            triggerCompleted(Node.CONNECTION_DEFAULT_TYPE, true);
        }
    }
    
    private String copy(String fromValue, String fromPart, String toValue, String toPart) {
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		Document fromDocument = factory.newDocumentBuilder().parse(new ByteArrayInputStream(fromValue.getBytes()));
        	Element from = DOMUtils.findChildByName((Element) fromDocument.getDocumentElement(), new QName(fromPart));
    		Document toDocument = factory.newDocumentBuilder().parse(new ByteArrayInputStream(toValue.getBytes()));
        	Element to = DOMUtils.findChildByName((Element) toDocument.getDocumentElement(), new QName(toPart));
        	
        	Element replacement = toDocument.createElementNS(from.getNamespaceURI(), from.getNodeName());
            NodeList nl = from.getChildNodes();
            for (int i = 0; i < nl.getLength(); ++i)
                replacement.appendChild(toDocument.importNode(nl.item(i), true));
            NamedNodeMap attrs = from.getAttributes();
            for (int i = 0; i < attrs.getLength(); ++i) {
                if (!((Attr)attrs.item(i)).getName().startsWith("xmlns")) {
                    replacement.setAttributeNodeNS((Attr) toDocument.importNode(attrs.item(i), true));
                }
            }
        	to.getParentNode().replaceChild(replacement, to);
        	return DOMUtils.domToString(toDocument.getDocumentElement());
        } catch (Throwable t) {
        	throw new IllegalArgumentException("Could not copy value", t);
        }
    }
    
    private String initializeVariable(String variable) {
    	// TODO
    	return "<shippingRequestMessage><customerInfo></customerInfo></shippingRequestMessage>";
    }
    
    private String getVariableValue(String variable) {
    	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
    		resolveContextInstance(VariableScope.VARIABLE_SCOPE, variable);
    	if (variableScopeInstance != null) {
            return (String) variableScopeInstance.getVariable(variable);
        } else {
            System.err.println("Could not find variable scope for variable " + variable);
            System.err.println("when trying assign");
            System.err.println("Continuing without setting variable.");
        }
    	return null;
    }
    
    private void setVariableValue(String variable, String value) {
    	VariableScopeInstance variableScopeInstance = (VariableScopeInstance)
    		resolveContextInstance(VariableScope.VARIABLE_SCOPE, variable);
    	if (variableScopeInstance != null) {
            variableScopeInstance.setVariable(variable, value);
        } else {
            System.err.println("Could not find variable scope for variable " + variable);
            System.err.println("when trying assign");
            System.err.println("Continuing without setting variable.");
        }
    }
    
    public void triggerCompleted(String type, boolean remove) {
        super.triggerCompleted(type, remove);
        BPELLinkManager.activateTargetLinks(this);
    }
    
}
