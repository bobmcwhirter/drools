package org.drools.bpmn2.xml;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.SequenceFlow;
import org.drools.compiler.xml.ProcessBuildData;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.WorkItemNode;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PropertyHandler extends BaseAbstractHandler implements Handler {

	public PropertyHandler() {
        initValidParents();
        initValidPeers();
        this.allowNesting = false;
    }
    
    protected void initValidParents() {
        this.validParents = new HashSet<Class<?>>();
        this.validParents.add(ContextContainer.class);
        this.validParents.add(WorkItemNode.class);
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet<Class<?>>();
        this.validPeers.add(null);
        this.validPeers.add(Variable.class);
        this.validPeers.add(Node.class);
        this.validPeers.add(SequenceFlow.class);
    }
    
	@SuppressWarnings("unchecked")
	public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		final String id = attrs.getValue("id");
		final String itemSubjectRef = attrs.getValue("itemSubjectRef");

		Object parent = parser.getParent();
		if (parent instanceof ContextContainer) {
		    ContextContainer contextContainer = (ContextContainer) parent;
		    VariableScope variableScope = (VariableScope) 
                contextContainer.getDefaultContext(VariableScope.VARIABLE_SCOPE);
			List variables = variableScope.getVariables();
			Variable variable = new Variable();
			variable.setName(id);
			// retrieve type from item definition
			DataType dataType = new ObjectDataType();
			Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
	            ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
	        if (itemDefinitions != null) {
	        	ItemDefinition itemDefinition = itemDefinitions.get(itemSubjectRef);
	        	if (itemDefinition != null) {
	        		dataType = new ObjectDataType(itemDefinition.getStructureRef());
	        	}
	        }
			variable.setType(dataType);
			variables.add(variable);
			return variable;
		}

		return new Variable();
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Variable.class;
	}

}
