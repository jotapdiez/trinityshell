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

package org.trinity.shellplugin.wm.x11.impl;

import org.apache.onami.autobind.annotations.GuiceModule;
import org.trinity.shell.api.bindingkey.ShellRootNode;
import org.trinity.shell.api.scene.ShellNodeParent;
import org.trinity.shellplugin.wm.api.Desktop;
import org.trinity.shellplugin.wm.x11.impl.scene.ClientBarElement;
import org.trinity.shellplugin.wm.x11.impl.scene.ClientBarElementFactory;
import org.trinity.shellplugin.wm.x11.impl.scene.ShellRootWidget;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

@GuiceModule
class Module extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(	ClientBarElement.class,
														ClientBarElement.class).build(ClientBarElementFactory.class));
		bind(ShellRootWidget.class).asEagerSingleton();
		bind(ShellNodeParent.class).annotatedWith(ShellRootNode.class).to(ShellRootWidget.class);
		bind(Desktop.class).to(ShellRootWidget.class);
	}
}
