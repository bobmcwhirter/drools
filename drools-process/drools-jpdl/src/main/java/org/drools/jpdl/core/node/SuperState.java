/*
 *  Copyright 2009 salaboy.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.drools.jpdl.core.node;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author salaboy
 */
public class SuperState extends JpdlNode{
    private List<JpdlNode> nodes;

    public List<JpdlNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<JpdlNode> nodes) {
        this.nodes = nodes;
    }
    public void addNode(JpdlNode node){
        if(nodes == null){
            nodes = new ArrayList<JpdlNode>();
        }
        nodes.add(node);
    }

 
}
