package org.drools.bpmn2.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.Message;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MessageHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public MessageHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(null);

			this.validPeers = new HashSet();
			this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);

			this.allowNesting = false;
		}
	}

	@SuppressWarnings("unchecked")
    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String structureRef = attrs.getValue("structureRef");
		
		Map<String, ItemDefinition> itemDefinitions = (Map<String, ItemDefinition>)
            ((ProcessBuildData) parser.getData()).getMetaData("ItemDefinitions");
        if (itemDefinitions == null) {
            throw new IllegalArgumentException("No item definitions found");
        }
        ItemDefinition itemDefinition = itemDefinitions.get(structureRef);
        if (itemDefinition == null) {
            throw new IllegalArgumentException("Could not find itemDefinition " + structureRef);
        }
        
        ProcessBuildData buildData = (ProcessBuildData) parser.getData();
		Map<String, Message> messages = (Map<String, Message>)
            ((ProcessBuildData) parser.getData()).getMetaData("Messages");
        if (messages == null) {
            messages = new HashMap<String, Message>();
            buildData.setMetaData("Messages", messages);
        }
        Message message = new Message(id); 
        message.setType(itemDefinition.getStructureRef());
        messages.put(id, message);
		return message;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Message.class;
	}

}