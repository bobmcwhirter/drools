package org.drools.task.service;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.task.AccessType;

public class ContentData implements Externalizable {
	
	private AccessType accessType;
	private String type;
	private byte[] content;

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( accessType );
        out.writeUTF( type );
        out.writeInt( content.length );
        out.write( content );        
    }
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        accessType = (AccessType) in.readObject();
        type = in.readUTF();
        content = new byte[ in.readInt() ];
        in.read( content );
    }

}
