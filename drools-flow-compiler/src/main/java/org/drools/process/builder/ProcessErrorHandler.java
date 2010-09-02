package org.drools.process.builder;

import org.drools.compiler.DroolsError;
import org.drools.compiler.ProcessBuildError;
import org.drools.compiler.PackageBuilder.ErrorHandler;
import org.drools.definition.process.Process;
import org.drools.lang.descr.BaseDescr;

public class ProcessErrorHandler extends ErrorHandler {

    private BaseDescr descr;

    private Process   process;

    public ProcessErrorHandler(final BaseDescr ruleDescr,
                               final Process process,
                               final String message) {
        this.descr = ruleDescr;
        this.process = process;
        this.message = message;
    }

    public DroolsError getError() {
        return new ProcessBuildError( this.process,
                                      this.descr,
                                      collectCompilerProblems(),
                                      this.message );
    }

}