package org.drools.task.service;

import java.io.IOException;

public interface SessionWriter {
	
	public void write(Object message) throws IOException;

}
