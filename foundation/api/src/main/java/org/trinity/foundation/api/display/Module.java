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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.onami.autobind.annotations.GuiceModule;
import org.trinity.foundation.api.display.bindkey.DisplayExecutor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.AbstractModule;

@GuiceModule
class Module extends AbstractModule {
	@Override
	protected void configure() {
		bind(ListeningExecutorService.class).annotatedWith(DisplayExecutor.class)
				.toInstance(MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(final Runnable executorRunnable) {
						return new Thread(	executorRunnable,
											"display-executor");
					}
				})));
	}
}
