package org.trinity.shell.api;

import org.trinity.shell.api.plugin.ShellPlugin;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import de.devsurf.injection.guice.annotations.GuiceModule;

/***************************************
 * Registers useful objects in the Guice injection framework. The following
 * objects are registered:
 * <p>
 * <ul>
 * <li>An {@link EventBus} which is {@link Named} "shellEventBus". This eventbus
 * is the primary means of communication between independent {@link ShellPlugin}
 * s and informs any subscribed listener to changes of the shell. This eventbus
 * is driven by a single shell thread, subscribers should thus not block their
 * handling of notifications.
 * </ul>
 * 
 *************************************** 
 */
@GuiceModule
public class Module extends AbstractModule {

	@Override
	protected void configure() {
		bind(EventBus.class).annotatedWith(Names.named("shellEventBus")).toInstance(new EventBus());
	}
}
