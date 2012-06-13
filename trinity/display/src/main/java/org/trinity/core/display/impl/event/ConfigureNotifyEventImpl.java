package org.trinity.core.display.impl.event;

import org.trinity.foundation.display.api.event.ConfigureNotifyEvent;
import org.trinity.foundation.display.api.event.DisplayEventSource;
import org.trinity.foundation.display.api.event.DisplayEventType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class ConfigureNotifyEventImpl extends DisplayEventImpl implements
		ConfigureNotifyEvent {

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	@Inject
	protected ConfigureNotifyEventImpl(	@Named("ConfigureNotify") final DisplayEventType configureNotify,
										@Assisted final DisplayEventSource eventSource,
										@Assisted("x") final int x,
										@Assisted("y") final int y,
										@Assisted("width") final int width,
										@Assisted("height") final int height) {
		super(configureNotify, eventSource);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

}
