package org.trinity.bootstrap;

import dagger.Module;
import dagger.ObjectGraph;
import dagger.Provides;
import org.trinity.wayland.protocol.WlProtocolModule;
import org.trinity.x11.X11Module;

import javax.inject.Singleton;

@Module(
		injects = {
				EntryPoint.class,
				ObjectGraph.class
		},
		includes = {
				X11Module.class,
                WlProtocolModule.class
				//BindingDefaultModule.class
		},
		complete = true,
		library = false
)
public class TrinityShellModule {

	private ObjectGraph objectGraph;

	void setObjectGraph(final ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}

	@Provides
	@Singleton
	ObjectGraph provideObjectGraph() {
		return this.objectGraph;
	}
}