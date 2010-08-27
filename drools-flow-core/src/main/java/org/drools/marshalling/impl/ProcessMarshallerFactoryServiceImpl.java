package org.drools.marshalling.impl;

public class ProcessMarshallerFactoryServiceImpl implements ProcessMarshallerFactoryService {

	public ProcessMarshaller newProcessMarshaller() {
		return new ProcessMarshallerImpl();
	}
	
}
