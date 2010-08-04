package org.drools.grid.services.factory;

import org.drools.grid.services.configuration.MinaProvider;
import org.drools.grid.services.configuration.RioProvider;

public interface GenericProviderContainerBuilder<T> {

	T onLocalProvider();

	T onMinaProvider(MinaProvider provider);

	T onHornetQProvider();

	T onRioProvider(RioProvider provider);

}
