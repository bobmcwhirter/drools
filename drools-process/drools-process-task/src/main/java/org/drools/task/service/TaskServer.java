package org.drools.task.service;

public abstract class TaskServer implements Runnable {
	
	public abstract void start() throws Exception;
	public abstract void stop() throws Exception;

}
