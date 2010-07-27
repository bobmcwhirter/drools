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

import org.drools.workflow.core.Node;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface BPELActivity extends Node {
	
	// TODO
	// String getJoinCondition();
	// void setJoinCondition(String joinCondition);
	
	// TODO
	// boolean isSuppressJoinFailure();
	// void setSuppressJoinFailure(boolean suppressJoinFailure);

    SourceLink[] getSourceLinks();
    
    void setSourceLinks(SourceLink[] sourceLinks);
    
    TargetLink[] getTargetLinks();
    
    void setTargetLinks(TargetLink[] targetLinks);
    
    public class SourceLink {
    	
    	private String linkName;
    	private String transitionCondition;
    	
		public SourceLink(String linkName) {
			this.linkName = linkName;
		}
		
		public String getLinkName() {
			return linkName;
		}
		
		public String getTransitionCondition() {
			return transitionCondition;
		}
		
		public void setTransitionCondition(String transitionCondition) {
			this.transitionCondition = transitionCondition;
		}
		
    }
    
    public class TargetLink {
    	
    	private String linkName;
    	
    	public TargetLink(String linkName) {
    		this.linkName = linkName;
    	}

		public String getLinkName() {
			return linkName;
		}

    }
    
}
