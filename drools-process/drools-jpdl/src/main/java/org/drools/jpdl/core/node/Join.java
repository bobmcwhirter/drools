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

import java.util.Collection;

import org.jbpm.graph.action.Script;

public class Join extends JpdlNode {

    private static final long serialVersionUID = 1L;
    
    private String parentLockMode;
    private boolean isDiscriminator = false;
    private Collection<String> tokenNames = null;
    private Script script = null;
    private int nOutOfM = -1;
    
    public String getParentLockMode() {
        return parentLockMode;
    }
    
    public void setParentLockMode(String parentLockMode) {
        this.parentLockMode = parentLockMode;
    }
    
    public boolean isDiscriminator() {
        return isDiscriminator;
    }
    
    public void setDiscriminator(boolean isDiscriminator) {
        this.isDiscriminator = isDiscriminator;
    }
    
    public Collection<String> getTokenNames() {
        return tokenNames;
    }
    
    public void setTokenNames(Collection<String> tokenNames) {
        this.tokenNames = tokenNames;
    }
    
    public Script getScript() {
        return script;
    }
    
    public void setScript(Script script) {
        this.script = script;
    }
    
    public int getNOutOfM() {
        return nOutOfM;
    }
    
    public void setNOutOfM(int outOfM) {
        nOutOfM = outOfM;
    }

}
