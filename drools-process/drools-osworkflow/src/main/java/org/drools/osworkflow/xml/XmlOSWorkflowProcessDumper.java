package org.drools.osworkflow.xml;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;

import org.drools.definition.process.WorkflowProcess;
import org.drools.osworkflow.core.OSWorkflowProcess;
import org.drools.xml.XmlWorkflowProcessDumper;

import com.opensymphony.workflow.loader.ActionDescriptor;

public class XmlOSWorkflowProcessDumper extends XmlWorkflowProcessDumper {

    public static final XmlOSWorkflowProcessDumper INSTANCE = new XmlOSWorkflowProcessDumper();
    private final static String EOL = System.getProperty( "line.separator" );
    private XmlOSWorkflowProcessDumper() {
        super(
            "OSWorkflow",
            "http://drools.org/drools-4.0/osworkflow",
            "drools-osworkflow-4.0.xsd",
            new OSWorkflowSemanticModule()
        );
    }
    @Override
    protected void visitHeader(WorkflowProcess process, StringBuilder xmlDump, boolean includeMeta) {
        xmlDump.append("  <header>" + EOL);
        visitInitialActions(((OSWorkflowProcess) process).getInitialActions(), xmlDump);
        
        xmlDump.append("  </header>" + EOL + EOL);
    }
    private void visitInitialActions(Collection<ActionDescriptor> initialActions, StringBuilder xmlDump) {
        if (initialActions != null && initialActions.size() > 0) {
            xmlDump.append("<initial-actions>" + EOL);
            for (ActionDescriptor action: initialActions) {
            	StringWriter stringWriter = new StringWriter();
            	PrintWriter writer = new PrintWriter(stringWriter);
                action.writeXML(writer, 3);
                writer.close();
                xmlDump.append(stringWriter.toString());
            }
            xmlDump.append("    </initial-actions>" + EOL);
        }
    }
    
}
