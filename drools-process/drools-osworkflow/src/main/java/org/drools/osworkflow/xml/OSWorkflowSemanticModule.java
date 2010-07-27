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

package org.drools.osworkflow.xml;

import org.drools.xml.DefaultSemanticModule;
import org.drools.xml.processes.ActionNodeHandler;
import org.drools.xml.processes.CompositeNodeHandler;
import org.drools.xml.processes.ConnectionHandler;
import org.drools.xml.processes.ConstraintHandler;
import org.drools.xml.processes.EndNodeHandler;
import org.drools.xml.processes.GlobalHandler;
import org.drools.xml.processes.ImportHandler;
import org.drools.xml.processes.InPortHandler;
import org.drools.xml.processes.JoinNodeHandler;
import org.drools.xml.processes.MappingHandler;
import org.drools.xml.processes.MilestoneNodeHandler;
import org.drools.xml.processes.OutPortHandler;
import org.drools.xml.processes.ParameterHandler;
import org.drools.xml.processes.RuleSetNodeHandler;
import org.drools.xml.processes.SplitNodeHandler;
import org.drools.xml.processes.StartNodeHandler;
import org.drools.xml.processes.SubProcessNodeHandler;
import org.drools.xml.processes.TimerNodeHandler;
import org.drools.xml.processes.TypeHandler;
import org.drools.xml.processes.ValueHandler;
import org.drools.xml.processes.VariableHandler;
import org.drools.xml.processes.WorkHandler;
import org.drools.xml.processes.WorkItemNodeHandler;

public class OSWorkflowSemanticModule extends DefaultSemanticModule {
    
    public OSWorkflowSemanticModule() {
        super ( "http://drools.org/drools-4.0/osworkflow" );

        addHandler( "process",
                           new ProcessHandler() );
        addHandler( "start",
                           new StartNodeHandler() );
        addHandler( "end",
                           new EndNodeHandler() );
        addHandler( "initial-actions",
        				new InitialActionsHandler() );
//        addHandler( "action",
//                           new ActionNodeHandler() );
        addHandler( "ruleSet",
                           new RuleSetNodeHandler() );
        addHandler( "subProcess",
                           new SubProcessNodeHandler() );
        addHandler( "workItem",
                           new WorkItemNodeHandler() );
        addHandler( "split",
                           new SplitNodeHandler() );
        addHandler( "join",
                           new JoinNodeHandler() );
        addHandler( "milestone",
                           new MilestoneNodeHandler() );
        addHandler( "timer",
                           new TimerNodeHandler() );
        addHandler( "composite",
                           new CompositeNodeHandler() );
        addHandler( "step",
                           new StepNodeHandler() );
        addHandler( "connection",
                           new ConnectionHandler() );
        addHandler( "import",
                           new ImportHandler() );
        addHandler( "global",
                           new GlobalHandler() );        
        addHandler( "variable",
                           new VariableHandler() );        
        addHandler( "type",
                           new TypeHandler() );        
        addHandler( "value",
                           new ValueHandler() );        
        addHandler( "work",
                           new WorkHandler() );        
        addHandler( "parameter",
                           new ParameterHandler() );        
        addHandler( "mapping",
                           new MappingHandler() );        
        addHandler( "constraint",
                           new ConstraintHandler() );        
        addHandler( "in-port",
                           new InPortHandler() );        
        addHandler( "out-port",
                           new OutPortHandler() );
    }

}
