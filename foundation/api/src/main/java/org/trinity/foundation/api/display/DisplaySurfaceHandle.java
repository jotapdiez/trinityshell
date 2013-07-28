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
package org.trinity.foundation.api.display;

import org.trinity.foundation.api.display.bindkey.DisplayExecutor;
import org.trinity.foundation.api.shared.ExecutionContext;

import javax.annotation.concurrent.ThreadSafe;

/***************************************
 * Wraps a native handle to an underlying native display resource. Access to the
 * native handle is done by calling {@link #getNativeHandle()}.
 *
 ***************************************
 */
@ExecutionContext(DisplayExecutor.class)
@ThreadSafe
public interface DisplaySurfaceHandle {

	/***************************************
	 * The wrapped native display resource handle.
	 *
	 * @return An object, implementation dependent but usually an
	 *         {@link Integer} .
	 ***************************************
	 */
	Object getNativeHandle();
}
