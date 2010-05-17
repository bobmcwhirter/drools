package org.drools.bpmn2.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.bpmn2.core.Definitions;
import org.drools.bpmn2.core.Error;
import org.drools.bpmn2.core.Escalation;
import org.drools.bpmn2.core.Interface;
import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.Message;
import org.drools.compiler.xml.ProcessBuildData;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ErrorHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public ErrorHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(Definitions.class);

			this.validPeers = new HashSet();
			this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);
            this.validPeers.add(Escalation.class);
            this.validPeers.add(Error.class);

			this.allowNesting = false;
		}
	}

	@SuppressWarnings("unchecked")
    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String errorCode = attrs.getValue("errorCode");

		ProcessBuildData buildData = (ProcessBuildData) parser.getData();
		Map<String, Error> errors = (Map<String, Error>) buildData.getMetaData("Errors");
        if (errors == null) {
        	errors = new HashMap<String, Error>();
            buildData.setMetaData("Errors", errors);
        }
        Error e = new Error(id, errorCode); 
        errors.put(id, e);
		return e;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Error.class;
	}

}