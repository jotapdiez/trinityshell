/*******************************************************************************
 * Trinity Shell Copyright (C) 2011 Erik De Rijcke
 *
 * This file is part of Trinity Shell.
 *
 * Trinity Shell is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * Trinity Shell is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 ******************************************************************************/

package org.trinity.shell.surface.impl;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.onami.autobind.annotations.Bind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.shared.AsyncListenable;
import org.trinity.shell.api.bindingkey.ShellExecutor;
import org.trinity.shell.api.bindingkey.ShellScene;
import org.trinity.shell.api.scene.ShellNodeParent;
import org.trinity.shell.api.surface.ShellSurface;
import org.trinity.shell.api.surface.ShellSurfaceFactory;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

@Bind
@Singleton
@ThreadSafe
public class ShellSurfaceFactoryImpl implements ShellSurfaceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ShellSurfaceFactory.class);
	private final ShellNodeParent shellRootNode;
	private final AsyncListenable shellScene;
	private final ListeningExecutorService shellExecutor;

	@Inject
	ShellSurfaceFactoryImpl(@Nonnull @ShellRootNode final ShellNodeParent shellRootNode,
							@ShellScene final AsyncListenable shellScene,
							@ShellExecutor final ListeningExecutorService shellExecutor) {
		this.shellRootNode = shellRootNode;
		this.shellScene = shellScene;
		this.shellExecutor = shellExecutor;
	}

	@Override
	public ListenableFuture<ShellSurface> createShellSurface(@Nonnull final DisplaySurface clientDisplaySurface) {
		return this.shellExecutor.submit(new Callable<ShellSurface>() {
			@Override
			public ShellSurface call() {
				final ShellSurfaceImpl shellSurfaceImpl = new ShellSurfaceImpl(	shellRootNode,
																				shellScene,
																				shellExecutor,
																				clientDisplaySurface);
				shellSurfaceImpl.syncGeoToDisplaySurface();
				clientDisplaySurface.register(	shellSurfaceImpl,
												shellExecutor);

				return shellSurfaceImpl;
			}
		});
	}

}
