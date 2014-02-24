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

package org.trinity.shell.api;

import com.google.common.eventbus.EventBus;
import dagger.Module;
import org.trinity.shell.api.plugin.ShellPlugin;

/***************************************
 * Registers useful objects in the Guice injection framework. The following
 * objects are registered:
 * <p>
 * <ul>
 * <li>An {@link EventBus} which is {@link Named} "ShellEventBus". This eventbus
 * is the primary means of communication between independent {@link ShellPlugin}
 * s and informs any subscribed listener to changes of the shell. This eventbus
 * is driven by a single shell thread, subscribers should thus not block their
 * handling of notifications.
 * </ul>
 *
 ***************************************
 */
@Module
class ShellApiModule {
//TODO
}