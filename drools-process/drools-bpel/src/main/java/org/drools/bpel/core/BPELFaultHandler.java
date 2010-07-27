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

package org.drools.bpel.core;

import org.drools.process.core.context.exception.ExceptionHandler;

/**
 * A BPEL FaultHandler.
 * A catchAll faultHandler has a null faultName.
 *  
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELFaultHandler implements ExceptionHandler {
    
    private String faultName;
    private String faultVariable;
    private BPELActivity activity;
    
    public String getFaultName() {
        return faultName;
    }
    
    public void setFaultName(String faultName) {
        this.faultName = faultName;
    }
    
    public String getFaultVariable() {
        return faultVariable;
    }
    
    public void setFaultVariable(String faultVariable) {
        this.faultVariable = faultVariable;
    }
    
    public BPELActivity getActivity() {
        return activity;
    }
    
    public void setActivity(BPELActivity activity) {
        this.activity = activity;
    }

}
