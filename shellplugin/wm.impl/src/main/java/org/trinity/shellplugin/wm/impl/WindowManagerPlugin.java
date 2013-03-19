package org.trinity.shellplugin.wm.impl;

import java.util.concurrent.Callable;

import org.trinity.foundation.api.display.DisplayServer;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.display.event.CreationNotify;
import org.trinity.shell.api.plugin.ShellPlugin;
import org.trinity.shell.api.surface.ShellSurface;
import org.trinity.shell.api.surface.ShellSurfaceFactory;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;

import static com.google.common.util.concurrent.Futures.addCallback;

@Bind(multiple = true)
public class WindowManagerPlugin extends AbstractIdleService implements ShellPlugin {

	private final DisplayServer displayServer;
	private final Scene scene;
	private final ListeningExecutorService wmExecutor;
	private final ShellSurfaceFactory shellSurfaceFactory;

	@Inject
	WindowManagerPlugin(@Named("WmExecutor") final ListeningExecutorService wmExecutor,
						final Scene scene,
						final ShellSurfaceFactory shellSurfaceFactory,
						final DisplayServer displayServer) {
		this.displayServer = displayServer;
		this.scene = scene;
		this.wmExecutor = wmExecutor;
		this.shellSurfaceFactory = shellSurfaceFactory;
	}

	@Override
	protected void startUp() throws Exception {
		this.displayServer.addListener(this);
	}

	@Override
	protected void shutDown() throws Exception {
		this.displayServer.removeListener(this);
	}

	@Subscribe
	public final void handleCreationNotify(final CreationNotify creationNotify) {
		this.wmExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				return handleCreationNotifyImpl(creationNotify);
			}
		});
	}

	public Void handleCreationNotifyImpl(final CreationNotify creationNotify) {

		final DisplaySurface displaySurface = creationNotify.getDisplaySurface();
		final ListenableFuture<ShellSurface> shellSurfaceFuture = this.shellSurfaceFactory
				.createShellClientSurface(displaySurface);

		addCallback(shellSurfaceFuture,
					new FutureCallback<ShellSurface>() {
						@Override
						public void onSuccess(final ShellSurface result) {
							handleShellSurface(result);
						}

						@Override
						public void onFailure(final Throwable t) {
							// TODO Auto-generated method stub
							t.printStackTrace();
						}
					},
					this.wmExecutor);

		return null;
	}

	public void handleShellSurface(final ShellSurface shellSurface) {
		this.scene.addClient(shellSurface);
	}
}
