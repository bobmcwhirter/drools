package org.drools.task.event;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.drools.WorkingMemory;
import org.drools.common.InternalWorkingMemory;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;


public class TaskEventSupport
    implements
    Externalizable {
    /**
     *
     */
    private static final long serialVersionUID = 400L;
    private List<TaskEventListener> listeners = new CopyOnWriteArrayList<TaskEventListener>();

    public TaskEventSupport() {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        listeners   = (List<TaskEventListener>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(listeners);
    }

    public void addEventListener(final TaskEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final TaskEventListener listener) {
        this.listeners.remove( listener );
    }

    public List<TaskEventListener> getEventListeners() {
        return Collections.unmodifiableList( this.listeners );
    }

    public int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }
    
    public void fireTaskClaimed(final long taskId, final long userId) {
      if ( this.listeners.isEmpty() ) {
          return;
      }

      final TaskClaimedEvent event = new TaskClaimedEvent(taskId, userId);
      
      for ( TaskEventListener listener: listeners) {
          listener.taskClaimed( event );
      }
  }    

    public void fireTaskClosed(final long taskId, final long userId) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final TaskClosedEvent event = new TaskClosedEvent( taskId, userId );
        
        for ( TaskEventListener listener: listeners) {
            listener.taskClosed( event );
        }
    } 
    
    

    public void reset() {
        this.listeners.clear();
    }
}