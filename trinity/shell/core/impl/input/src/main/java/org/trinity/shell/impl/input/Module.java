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
package org.trinity.shell.impl.input;

import org.trinity.shell.api.input.KeysBindingFactory;
import org.trinity.shell.api.input.ShellKeysBinding;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

import de.devsurf.injection.guice.annotations.GuiceModule;

@GuiceModule
public class Module extends AbstractModule {

	@Override
	protected void configure() {
		install(new FactoryModuleBuilder().implement(	ShellKeysBinding.class,
														ShellKeysBindingImpl.class).build(KeysBindingFactory.class));
	}
}