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
package org.trinity.render.paintengine.qt.impl.eventconverters;

import org.trinity.foundation.display.api.event.ButtonNotifyEvent;
import org.trinity.foundation.display.api.event.DisplayEventSource;
import org.trinity.foundation.input.api.Button;
import org.trinity.foundation.input.api.InputModifiers;
import org.trinity.foundation.input.api.Momentum;
import org.trinity.foundation.input.api.PointerInput;
import org.trinity.render.paintengine.qt.impl.QFRenderEventConverter;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.gui.QMouseEvent;

import de.devsurf.injection.guice.annotations.Bind;

@Bind(multiple = true)
@Singleton
public final class QFButtonPressedConverterImpl implements
		QFRenderEventConverter {

	@Inject
	@Named("MouseButtonPress")
	private QEvent.Type qType;

	@Override
	public ButtonNotifyEvent convertEvent(	final DisplayEventSource source,
											final QEvent qEvent) {
		qEvent.accept();

		final QMouseEvent qMouseEvent = (QMouseEvent) qEvent;

		final int buttonCode = qMouseEvent.button().value();
		final int state = qMouseEvent.modifiers().value();

		final int rootX = qMouseEvent.globalX();
		final int rootY = qMouseEvent.globalY();
		final int relativeX = qMouseEvent.x();
		final int relativeY = qMouseEvent.y();

		final Button button = new Button(buttonCode);
		final InputModifiers inputModifiers = new InputModifiers(state);
		final PointerInput mouseInput = new PointerInput(	Momentum.STARTED,
															button,
															inputModifiers,
															relativeX,
															relativeY,
															rootX,
															rootY);

		return new ButtonNotifyEvent(source, mouseInput);
	}

	/*
	 * (non-Javadoc)
	 * @see org.fusion.qt.paintengine.QFusionEventConverter#getFromType()
	 */
	@Override
	public Type getQEventType() {
		return this.qType;
	}
}