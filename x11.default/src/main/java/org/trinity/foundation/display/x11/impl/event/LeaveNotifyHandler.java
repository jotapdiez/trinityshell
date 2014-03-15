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
package org.trinity.foundation.display.x11.impl.event;

import org.freedesktop.xcb.xcb_enter_notify_event_t;
import org.freedesktop.xcb.xcb_generic_event_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.display.x11.impl.XEventChannel;
import org.trinity.foundation.display.x11.impl.XEventHandler;
import org.trinity.foundation.display.x11.impl.XSurfacePool;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

import static org.freedesktop.xcb.LibXcbConstants.XCB_LEAVE_NOTIFY;

@Immutable
public class LeaveNotifyHandler implements XEventHandler {

	private static final Logger  LOG        = LoggerFactory.getLogger(LeaveNotifyHandler.class);
	private static final Integer EVENT_CODE = XCB_LEAVE_NOTIFY;
	private final XEventChannel xEventChannel;
	private final XSurfacePool xSurfacePool;

	@Inject
	LeaveNotifyHandler(final XEventChannel xEventChannel,
					   final XSurfacePool xSurfacePool) {
		this.xEventChannel = xEventChannel;
		this.xSurfacePool = xSurfacePool;
	}

	@Override
	public void handle(@Nonnull final xcb_generic_event_t event) {
		// enter has same structure as leave
		final xcb_enter_notify_event_t enter_notify_event = cast(event);

		LOG.debug("Received X event={}",
				  enter_notify_event.getClass().getSimpleName());

		this.xEventChannel.post(enter_notify_event);
		final int windowId = enter_notify_event.getEvent();
		this.xSurfacePool.get(windowId).post(enter_notify_event);
	}

	private xcb_enter_notify_event_t cast(final xcb_generic_event_t event) {
		return new xcb_enter_notify_event_t(xcb_generic_event_t.getCPtr(event),
											false);
	}

	@Override
	public Integer getEventCode() {
		return EVENT_CODE;
	}
}