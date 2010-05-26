package org.drools.task.service.hornetq;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.drools.task.service.SessionWriter;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;

public class HornetQSessionWriter implements SessionWriter {
	
	private final ClientSession session;
	private final ClientProducer producer;

	public HornetQSessionWriter(ClientSession session, ClientProducer producer) {
		this.session = session;
		this.producer = producer;
	}

	public void write(Object message) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oout;
		try {
			oout = new ObjectOutputStream(baos);
			oout.writeObject(message);
			ClientMessage clientMessage = session.createMessage(true);
			clientMessage.getBodyBuffer().writeBytes(baos.toByteArray());
			producer.send(clientMessage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (HornetQException e) {
			e.printStackTrace();
		}
	}

}
