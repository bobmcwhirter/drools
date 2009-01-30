package org.drools.osworkflow.dummy;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ProcessEntity {
	@Id
	private long id;
	private String test;

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
