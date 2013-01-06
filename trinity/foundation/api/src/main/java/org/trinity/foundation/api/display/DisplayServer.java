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
package org.trinity.foundation.api.display;

import org.trinity.foundation.api.display.event.DisplayEvent;

/**
 * Provides basic capabilities to talk to the underlying native display
 * functionality. One of the most fundamental aspects of the
 * <code>DisplayServer</code> is the queuing of {@link DisplayEvent}s.
 * <p>
 * {@link DisplayEvent}s generated by the underlying native display system are
 * fetched and placed on the internal queue by a {@link DisplayEventProducer}.
 * The fetching of a <code>DisplayEvent</code>s happens with a call to
 * {@link #getNextDisplayEvent()}.
 */
public interface DisplayServer {

	/***************************************
	 * The root {@link DisplaySurface} of the {@link DisplaySurface} hierarchy.
	 * 
	 * @return a {@link DisplaySurface}
	 *************************************** 
	 */
	DisplaySurface getRootDisplayArea();

	/**
	 * Indicates if there are any pending <code>DisplayEvent</code>s on the
	 * queue.
	 * 
	 * @return True if there are {@link DisplayEvent}s queued, false if not.
	 */
	boolean hasNextDisplayEvent();

	/**
	 * Retrieves ands removes the head {@link DisplayEvent} from the queue.
	 * 
	 * @return The next {@link DisplayEvent}.
	 */
	DisplayEvent getNextDisplayEvent();

	/**
	 * Orderly shut down this <code>DisplayServer</code>. All resources living
	 * on this <code>DisplayServer</code> will be shut down as well.
	 * <p>
	 * This method does not shut down the underlying native display, it merely
	 * closes the connection to the underlying native display.
	 */
	void shutDown();
}