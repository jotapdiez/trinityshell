/*
 * This file is part of Fusion-qtjambi. Fusion-qtjambi is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Fusion-qtjambi is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with Fusion-qtjambi. If not,
 * see <http://www.gnu.org/licenses/>.
 */
package org.trinity.render.paintengine.qt.impl;

import org.trinity.core.display.api.event.DisplayEventSource;
import org.trinity.core.render.api.RenderEventBridge;

import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.gui.QWidget;

/**
 * A <code>QFusionInputEventFilter</code> is installed on every QtJambi widget
 * (paint peer) that needs it's <code>QEvent</code>s converted.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
final class QFInputEventFilterImpl extends QObject {

	private final DisplayEventSource eventSource;
	private final RenderEventBridge<QEvent> renderEventBridget;

	QFInputEventFilterImpl(	final RenderEventBridge<QEvent> renderEventBridge,
							final DisplayEventSource eventSource,
							final QWidget eventProducingVisual) {
		this.renderEventBridget = renderEventBridge;
		this.eventSource = eventSource;
		eventProducingVisual.installEventFilter(this);
	}

	@Override
	public boolean eventFilter(final QObject qObject, final QEvent qEvent) {
		// Make sure we are interested in the event we receive.
		if (isEventSourceAndTypeValid(qObject, qEvent)) {
			// We are interested and thus convert the event.
			this.renderEventBridget.queueRenderEvent(this.eventSource, qEvent);
			return true;
		}
		return false;
	}

	private boolean isEventSourceAndTypeValid(	final QObject qObject,
												final QEvent qEvent) {
		return (qObject.isWidgetType() && ((qEvent.type() == Type.KeyPress)
				|| (qEvent.type() == Type.KeyRelease)
				|| (qEvent.type() == Type.MouseButtonPress)
				|| (qEvent.type() == Type.MouseButtonRelease)
				|| (qEvent.type() == Type.Enter)
				|| (qEvent.type() == Type.Leave)
				|| (qEvent.type() == Type.FocusIn) || (qEvent.type() == Type.FocusOut)));
	}
}
