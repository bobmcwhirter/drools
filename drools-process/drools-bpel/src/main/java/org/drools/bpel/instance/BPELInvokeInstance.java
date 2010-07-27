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

package org.drools.bpel.instance;

import org.drools.process.core.context.exception.ExceptionScope;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.context.exception.ExceptionScopeInstance;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.node.WorkItemNodeInstance;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELInvokeInstance extends WorkItemNodeInstance {
    
    private static final long serialVersionUID = 400L;

    public void internalTrigger(NodeInstance from, String type) {
        if (BPELLinkManager.checkActivityEnabled(this)) {
            super.internalTrigger(from, type);
        }
    }
    
    public void triggerCompleted(WorkItem workItem) {
        String faultName = (String) workItem.getResult("FaultName");
        if (faultName == null) {
            super.triggerCompleted(workItem);
            BPELLinkManager.activateTargetLinks(this);
        } else {
            String faultMessage = (String) workItem.getResult("Result");
            ExceptionScopeInstance exceptionScopeInstance = (ExceptionScopeInstance)
                resolveContextInstance(ExceptionScope.EXCEPTION_SCOPE, faultName);
            exceptionScopeInstance.handleException(faultName, faultMessage);
        }
    }
    
}
