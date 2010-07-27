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

import org.drools.workflow.core.Connection;

public class EndState extends JpdlNode {

	private static final long serialVersionUID = 1L;

    public void validateAddOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "An end state does not have an outgoing connection!");
    }

    public void validateRemoveOutgoingConnection(final String type, final Connection connection) {
        throw new UnsupportedOperationException(
            "An end state does not have an outgoing connection!");
    }
    
}
