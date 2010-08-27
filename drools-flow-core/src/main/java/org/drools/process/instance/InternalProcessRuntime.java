package org.drools.process.instance;

import org.drools.event.ProcessEventSupport;
import org.drools.process.instance.event.SignalManager;
import org.drools.process.instance.timer.TimerManager;

public interface InternalProcessRuntime extends org.drools.runtime.process.InternalProcessRuntime {
	
	ProcessInstanceManager getProcessInstanceManager();
	
	SignalManager getSignalManager();
	
	TimerManager getTimerManager();
	
	ProcessEventSupport getProcessEventSupport();

}
