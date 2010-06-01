package org.drools.task.service;

import java.util.concurrent.atomic.AtomicInteger;

public interface TaskClientConnector {
	
	public boolean connect();
	public boolean connect(String address, int port);
	public void disconnect() throws Exception;
	
	public void write(Object message);
	public BaseHandler getHandler();
	public String getName();
	public AtomicInteger getCounter();

}
