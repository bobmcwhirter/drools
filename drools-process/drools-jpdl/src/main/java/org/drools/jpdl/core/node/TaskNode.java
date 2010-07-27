/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.jpdl.core.node;

import java.util.Set;

import org.jbpm.taskmgmt.def.Task;

public class TaskNode extends JpdlNode {

	private static final long serialVersionUID = 1L;

	private Set<Task> tasks;
	private int signal = org.jbpm.graph.node.TaskNode.SIGNAL_LAST;
	private boolean createTasks = true;
	private boolean endTasks = false;
	
	public Set<Task> getTasks() {
		return tasks;
	}
	
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	
	public int getSignal() {
		return signal;
	}
	
	public void setSignal(int signal) {
		this.signal = signal;
	}
	
	public boolean isCreateTasks() {
		return createTasks;
	}
	
	public void setCreateTasks(boolean createTasks) {
		this.createTasks = createTasks;
	}
	
	public boolean isEndTasks() {
		return endTasks;
	}
	
	public void setEndTasks(boolean endTasks) {
		this.endTasks = endTasks;
	}
	  
}
