package org.drools.bpel.xpath;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.process.core.datatype.DataType;

public class XMLDataType implements DataType {

	private String typeDefinition;
	
	public XMLDataType(String typeDefinition) {
		this.typeDefinition = typeDefinition;
	}
	
	public boolean verifyDataType(Object value) {
		return value instanceof String;
	}

	public String getTypeDefinition() {
		return typeDefinition;
	}

	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		typeDefinition = input.readUTF();
	}

	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeUTF(typeDefinition);
	}

}
