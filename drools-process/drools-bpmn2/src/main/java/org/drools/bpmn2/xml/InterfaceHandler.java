package org.drools.bpmn2.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.drools.bpmn2.core.Interface;
import org.drools.bpmn2.core.ItemDefinition;
import org.drools.bpmn2.core.Message;
import org.drools.xml.BaseAbstractHandler;
import org.drools.xml.ExtensibleXmlParser;
import org.drools.xml.Handler;
import org.drools.xml.ProcessBuildData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class InterfaceHandler extends BaseAbstractHandler implements Handler {
	
	@SuppressWarnings("unchecked")
	public InterfaceHandler() {
		if ((this.validParents == null) && (this.validPeers == null)) {
			this.validParents = new HashSet();
			this.validParents.add(null);

			this.validPeers = new HashSet();
			this.validPeers.add(null);
            this.validPeers.add(ItemDefinition.class);
            this.validPeers.add(Message.class);
            this.validPeers.add(Interface.class);

			this.allowNesting = false;
		}
	}

	@SuppressWarnings("unchecked")
    public Object start(final String uri, final String localName,
			            final Attributes attrs, final ExtensibleXmlParser parser)
			throws SAXException {
		parser.startElementBuilder(localName, attrs);

		String id = attrs.getValue("id");
		String name = attrs.getValue("name");

		ProcessBuildData buildData = (ProcessBuildData) parser.getData();
		List<Interface> interfaces = (List<Interface>) buildData.getMetaData("Interfaces");
        if (interfaces == null) {
            interfaces = new ArrayList<Interface>();
            buildData.setMetaData("Interfaces", interfaces);
        }
        Interface i = new Interface(id, name); 
        interfaces.add(i);
		return i;
	}

	public Object end(final String uri, final String localName,
			          final ExtensibleXmlParser parser) throws SAXException {
		parser.endElementBuilder();
		return parser.getCurrent();
	}

	public Class<?> generateNodeFor() {
		return Interface.class;
	}

}