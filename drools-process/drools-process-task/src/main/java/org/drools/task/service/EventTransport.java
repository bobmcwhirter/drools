package org.drools.task.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.eventmessaging.EventTriggerTransport;
import org.drools.eventmessaging.Payload;

public class EventTransport implements EventTriggerTransport {

	private String uuid;
	private Map<String, SessionWriter> sessions;
	private int responseId;
	private boolean remove;

	public EventTransport(String uuid, int responseId, Map<String, SessionWriter> sessions, boolean remove) {
		this.uuid = uuid;
		this.responseId = responseId;
		this.sessions = sessions;
		this.remove = remove;
	}

	public void trigger(Payload payload) {        
		SessionWriter session = sessions.get( uuid );
		List<Object> args = new ArrayList<Object>( 1 );
		args.add(payload);
		Command cmd = new Command(responseId, CommandName.EventTriggerResponse, args);             
		session.write(cmd);        
	}

	public boolean isRemove() {
		return this.remove;
	}

}
