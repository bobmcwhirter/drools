package org.drools.osworkflow.test;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.osworkflow.DroolsWorkflow;
import org.drools.osworkflow.OSWorkflowParser;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.osworkflow.xml.XmlOSWorkflowProcessDumper;
import org.drools.xml.XmlProcessReader;

import com.opensymphony.workflow.InvalidActionException;
import com.opensymphony.workflow.InvalidEntryStateException;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.InvalidRoleException;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.config.DefaultConfiguration;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.Step;

public class WFPProcessTestCase extends TestCase {



	public void testToXML() throws Exception {
        DefaultConfiguration config = new DefaultConfiguration();
        config.load(null);
        WorkflowDescriptor workflowDescriptor = config.getWorkflow("wfp");
	    OSWorkflowProcess process = new OSWorkflowParser().parseOSWorkflow(workflowDescriptor);
	    
	    String processXML = XmlOSWorkflowProcessDumper.INSTANCE.dump(process);
	    System.out.println(processXML);
	    
	    XmlProcessReader reader = new XmlProcessReader(
            new PackageBuilderConfiguration().getSemanticModules());
	    System.setProperty( "drools.schema.validating", "false" );
	    OSWorkflowProcess process2 = (OSWorkflowProcess) reader.read(new StringReader(processXML));
        assertNotNull(process2);
	}
	
}
