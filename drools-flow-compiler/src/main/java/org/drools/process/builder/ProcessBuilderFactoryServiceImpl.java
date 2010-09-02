package org.drools.process.builder;

import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ProcessBuilderFactoryService;
import org.drools.compiler.ProcessBuilderImpl;

public class ProcessBuilderFactoryServiceImpl implements ProcessBuilderFactoryService {

	public ProcessBuilderImpl newProcessBuilder(PackageBuilder packageBuilder) {
		return new ProcessBuilderImpl(packageBuilder);
	}
	
}
