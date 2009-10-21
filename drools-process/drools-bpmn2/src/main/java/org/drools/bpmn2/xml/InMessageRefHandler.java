package org.drools.bpmn2.xml;

import java.util.HashSet;
import java.util.Map;

import org.drools.bpmn2.core.Message;
import org.drools.bpmn2.core.Interface.Operation;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class InMessageRefHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public InMessageRefHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(Operation.class);

			this.validPeers = new HashSet();
			this.validPeers.add(null);

			this.allowNesting = false;
		}
	}

    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);
		return null;
	}

	@SuppressWarnings("unchecked")
    public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		Element element = parser.endElementBuilder();
		String messageId = element.getTextContent();
		Map<String, Message> messages = (Map<String, Message>)
            ((ProcessBuildData) parser.getData()).getMetaData("Messages");
        if (messages == null) {
            throw new IllegalArgumentException("No messages found");
        }
        Message message = messages.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("Could not find message " + messageId);
        }
        Operation operation = (Operation) parser.getParent();
        operation.setMessage(message);
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Message.class;
	}

}