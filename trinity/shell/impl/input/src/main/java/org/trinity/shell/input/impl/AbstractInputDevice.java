/*
 * This file is part of HyperDrive. HyperDrive is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. HyperDrive is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with HyperDrive. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.trinity.shell.input.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.trinity.core.event.api.EventManager;
import org.trinity.foundation.display.api.event.InputNotifyEvent;
import org.trinity.foundation.input.api.Input;
import org.trinity.shell.input.api.InputDevice;

// TODO documentation
/**
 * A <code>GrabableInputDevice</code> is an input device who's input can be
 * redirected (grabbed) so that all input events originating from the device are
 * reported to the grabber.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public abstract class AbstractInputDevice implements InputDevice {
	private boolean grabbed;
	private final List<EventManager> inputEventManagers;

	/**
	 * @param managedDisplay
	 * @param grabEventTypes
	 */
	public AbstractInputDevice() {
		this.inputEventManagers = new CopyOnWriteArrayList<EventManager>();
	}

	/**
	 * @param grabber
	 * @throws HyperdriveException
	 */
	@Override
	public void grab() {
		if (isGrabbed()) {
			// TODO throw better exception
			throw new IllegalStateException("input device already grabbed.");
		} else {
			setGrabbed(true);
		}
		delegateInputEventsAndGrab();
	}

	protected abstract void delegateInputEventsAndGrab();

	protected void delegateInputEventToInputEventManagers(final InputNotifyEvent<? extends Input> displayEvent) {
		for (final EventManager inputEventManager : AbstractInputDevice.this.inputEventManagers) {
			inputEventManager.fireEvent(displayEvent);
		}
	}

	/**
	 * @return
	 */
	@Override
	public boolean isGrabbed() {
		return this.grabbed;
	}

	/**
	 * @param grabbed
	 */
	protected void setGrabbed(final boolean grabbed) {
		this.grabbed = grabbed;
	}

	@Override
	public void addInputEventManager(final EventManager inputEventManager) {
		this.inputEventManagers.add(inputEventManager);
	}

	@Override
	public void removeInputEventManager(final EventManager inputEventManager) {
		this.inputEventManagers.remove(inputEventManager);
	}
}
