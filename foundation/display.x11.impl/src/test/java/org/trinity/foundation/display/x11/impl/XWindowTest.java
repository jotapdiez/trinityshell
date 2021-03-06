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
package org.trinity.foundation.display.x11.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executors;

import org.freedesktop.xcb.LibXcb;
import org.freedesktop.xcb.LibXcbConstants;
import org.freedesktop.xcb.xcb_screen_iterator_t;
import org.freedesktop.xcb.xcb_screen_t;
import org.freedesktop.xcb.xcb_setup_t;
import org.freedesktop.xcb.xcb_window_class_t;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trinity.foundation.display.x11.api.XConnection;

import xcb4j.LibXcbLoader;

import com.google.common.util.concurrent.MoreExecutors;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class XWindowTest {

	private static final String displayName = ":99";
	private static final int screenNr = 0;
	public static XConnection xConnection;
	public static xcb_screen_t screen;
	private static Process xvfb;
	private XWindow xWindow;
	private int windowId;
	// mocks
	@Mock
	private XTime xTime;

	@BeforeClass
	public static void startup() throws IOException, SecurityException, NoSuchFieldException {
		LibXcbLoader.load();

		XWindowTest.xvfb = new ProcessBuilder(	"Xvfb",
												"-ac",
												XWindowTest.displayName).start();

		XWindowTest.xConnection = new XConnectionImpl();
		XWindowTest.xConnection.open(	XWindowTest.displayName,
										XWindowTest.screenNr);

		final xcb_setup_t setup = LibXcb.xcb_get_setup(XWindowTest.xConnection.getConnectionReference().get());
		final xcb_screen_iterator_t iter = LibXcb.xcb_setup_roots_iterator(setup);
		XWindowTest.screen = iter.getData();
	}

	@AfterClass
	public static void closedown() {

		XWindowTest.xConnection.close();
		XWindowTest.xvfb.destroy();
	}

	@Before
	public void setup() throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {

		this.windowId = LibXcb.xcb_generate_id(XWindowTest.xConnection.getConnectionReference().get());
		final ByteBuffer value_list = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());
		LibXcb.xcb_create_window(	XWindowTest.xConnection.getConnectionReference().get(),
									(short) LibXcbConstants.XCB_COPY_FROM_PARENT,
									this.windowId,
									XWindowTest.screen.getRoot(),
									(short) 0,
									(short) 0,
									200,
									200,
									5,
									xcb_window_class_t.XCB_WINDOW_CLASS_INPUT_OUTPUT,
									XWindowTest.screen.getRoot_visual(),
									0,
									value_list);

		final XWindowHandle xWindowHandle = new XWindowHandle(this.windowId);

		this.xWindow = new XWindow(	this.xTime,
									XWindowTest.xConnection,
									xWindowHandle,
									MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor()));
	}

	@After
	public void teardown() {
		// destroy xwindow instance
		LibXcb.xcb_destroy_window(	XWindowTest.xConnection.getConnectionReference().get(),
									this.windowId);
	}

	@Test
	public void testDestroy() {
		this.xWindow.destroy();
		// TODO verify
	}

	@Test
	public void testSetInputFocus() {
		this.xWindow.setInputFocus();
		// TODO verify
	}

	@Test
	public void testLower() {
		this.xWindow.lower();
	}

	@Test
	public void testShow() {
		this.xWindow.show();
		// TODO verify
	}

	@Test
	public void testMove() {
		this.xWindow.move(	10,
							10);
		// TODO verify
	}
}
