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

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELCorrelation {
	
	public static final int PATTERN_IN = 1;
	public static final int PATTERN_OUT = 2;
	public static final int PATTERN_OUT_IN = 3;
	
	private String set;
	private boolean initiate;
	private int pattern;
	
	public String getSet() {
		return set;
	}
	
	public void setSet(String set) {
		this.set = set;
	}
	
	public boolean isInitiate() {
		return initiate;
	}
	
	public void setInitiate(boolean initiate) {
		this.initiate = initiate;
	}
	
	public int getPattern() {
		return pattern;
	}
	
	public void setPattern(int pattern) {
		this.pattern = pattern;
	}

}
