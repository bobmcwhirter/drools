package org.drools.task.service.mina;

import org.apache.mina.core.session.IoSession;
import org.drools.task.service.SessionWriter;

public class MinaSessionWriter implements SessionWriter {
	
	private final IoSession session;

	public MinaSessionWriter(IoSession session) {
		this.session = session;
	}

	public void write(Object message) {
		session.write(message);
	}

}
