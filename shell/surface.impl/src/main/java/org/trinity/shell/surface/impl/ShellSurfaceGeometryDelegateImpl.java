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

import static com.google.common.base.Preconditions.checkArgument;

import org.trinity.foundation.api.display.DisplayAreaManipulator;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.shared.ExecutionContext;
import org.trinity.shell.api.bindingkey.ShellExecutor;
import org.trinity.shell.api.scene.AbstractShellNode;
import org.trinity.shell.api.scene.ShellNode;
import org.trinity.shell.api.scene.ShellNodeParent;
import org.trinity.shell.api.surface.AbstractAsyncShellSurface;
import org.trinity.shell.api.surface.AbstractShellSurface;
import org.trinity.shell.api.surface.AbstractShellSurfaceGeometryDelegate;
import org.trinity.shell.api.surface.ShellSurface;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

@ExecutionContext(ShellExecutor.class)
@NotThreadSafe
public class ShellSurfaceGeometryDelegateImpl extends AbstractShellSurfaceGeometryDelegate {

	private final AbstractShellSurface abstractShellSurface;

	ShellSurfaceGeometryDelegateImpl(@Nonnull final AbstractShellSurface shellSurface) {
		this.abstractShellSurface = shellSurface;
	}

	@Override
	protected DisplaySurface getSurfacePeer(@Nonnull final ShellSurface shellSurface) {
		checkArgument(shellSurface instanceof AbstractShellSurface);

		return ((AbstractAsyncShellSurface) shellSurface).getDisplaySurfaceImpl();
	}

	@Override
	protected AbstractShellSurface findClosestSameTypeSurface(@Nonnull final ShellNode square) {
		checkArgument(square instanceof AbstractShellNode);

		if (square == null) {
			return null;
		}

		final ShellNodeParent parent = ((AbstractShellNode) square).getParentImpl();
		if ((parent == null) || parent.equals(square)) {
			return null;
		}

		return findClosestSameTypeSurface(parent);
	}

	@Override
	public AbstractShellSurface getShellNode() {
		return this.abstractShellSurface;
	}

	@Override
	public DisplayAreaManipulator getShellNodeManipulator() {
		return getShellNode().getDisplaySurfaceImpl();
	}
}
