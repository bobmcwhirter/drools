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

package org.drools.bpel.xpath;

import org.drools.compiler.Dialect;
import org.drools.compiler.DialectConfiguration;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageRegistry;
import org.drools.rule.Package;

public class XPathDialectConfiguration implements DialectConfiguration {

	private PackageBuilderConfiguration packageBuilderConfiguration;
	
	public PackageBuilderConfiguration getPackageBuilderConfiguration() {
		return packageBuilderConfiguration;
	}

	public void init(PackageBuilderConfiguration configuration) {
		this.packageBuilderConfiguration = configuration;
	}

	public Dialect newDialect(PackageBuilder packageBuilder, PackageRegistry pkgRegistry, Package pkg) {
		return new XPathDialect();
	}

}
