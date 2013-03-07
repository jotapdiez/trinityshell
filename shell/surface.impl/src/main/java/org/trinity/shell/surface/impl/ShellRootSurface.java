/*
 * Trinity Window Manager and Desktop Shell Copyright (C) 2012 Erik De Rijcke
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.trinity.shell.surface.impl;

import org.trinity.foundation.api.display.DisplayServer;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.shell.api.scene.ShellNode;
import org.trinity.shell.api.scene.ShellNodeExecutor;
import org.trinity.shell.api.scene.ShellNodeParent;
import org.trinity.shell.api.surface.AbstractShellSurfaceParent;
import org.trinity.shell.api.surface.ShellDisplayEventDispatcher;
import org.trinity.shell.api.surface.ShellSurface;
import org.trinity.shell.api.surface.ShellSurfaceParent;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;
import de.devsurf.injection.guice.annotations.To;

@Bind(value = @Named("ShellRootSurface"), to = @To(value = To.Type.CUSTOM, customs = { ShellSurfaceParent.class,
		ShellSurface.class, ShellNodeParent.class, ShellNode.class }))
@Singleton
public class ShellRootSurface extends AbstractShellSurfaceParent {

	private final ShellNodeExecutor shellNodeExecutor;
	private final DisplaySurface displaySurface;

	@Inject
	ShellRootSurface(	final DisplayServer displayServer,
						final ShellDisplayEventDispatcher shellDisplayEventDispatcher) {
		this.shellNodeExecutor = new ShellSurfaceExecutorImpl(this);
		this.displaySurface = displayServer.getRootDisplayArea();
		syncGeoToDisplaySurface();
		shellDisplayEventDispatcher.registerDisplayEventTarget(	getNodeEventBus(),
																this.displaySurface);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public int getAbsoluteX() {
		return getX();
	}

	@Override
	public int getAbsoluteY() {
		return getY();
	}

	@Override
	public ShellRootSurface getParent() {
		return this;
	}

	@Override
	public ShellNodeExecutor getShellNodeExecutor() {
		return this.shellNodeExecutor;
	}

	@Override
	public DisplaySurface getDisplaySurface() {
		return this.displaySurface;
	}
}