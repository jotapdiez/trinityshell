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
package org.trinity.foundation.render.qt.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trolltech.qt.gui.QApplication;

public class RenderApplication extends QApplication {

	private static final Logger logger = LoggerFactory.getLogger(RenderApplication.class);

	public RenderApplication() {
		super(	"Trinity QtJambi Renderer",
				new String[] {});
	}

	private static final ExecutorService qRenderEventPump = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(final Runnable r) {
			return new Thread(	r,
								"qtjambi-application");
		}
	});

	public static void start() {
		qRenderEventPump.submit(new Runnable() {
			@Override
			public void run() {
				QApplication.initialize(new String[] {});
				QApplication.setQuitOnLastWindowClosed(false);
				final int r = QApplication.exec();
				if (r != 0) {
					logger.error(	"QtJambi render application exited with error code={}",
									r);
				}
			}
		});
		while (QApplication.startingUp()) {
			Thread.yield();
		}
	}
}