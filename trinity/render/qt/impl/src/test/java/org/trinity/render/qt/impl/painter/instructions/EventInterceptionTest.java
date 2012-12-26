package org.trinity.render.qt.impl.painter.instructions;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.trinity.foundation.display.api.event.ButtonNotifyEvent;
import org.trinity.foundation.display.api.event.DisplayEvent;
import org.trinity.foundation.display.api.event.DisplayEventTarget;
import org.trinity.foundation.display.api.event.FocusGainNotifyEvent;
import org.trinity.render.qt.impl.DummyQJRenderEngine;
import org.trinity.render.qt.impl.DummyView;
import org.trinity.render.qt.impl.QJRenderEventConverter;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.trolltech.qt.core.QEvent.Type;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.MouseButtons;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFocusEvent;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QWidget;

public class EventInterceptionTest {

	@Test
	public void testViewEvents() throws InterruptedException {
		final EventBus displayEventBus = mock(EventBus.class);

		final DisplayEventTarget eventSource = mock(DisplayEventTarget.class);
		final DisplayEvent focusGainNotifyEvent = new FocusGainNotifyEvent(eventSource);
		final Optional<DisplayEvent> optionalFocusGainNotifyEvent = Optional.of(focusGainNotifyEvent);
		final DisplayEvent buttonNotifyEvent = new ButtonNotifyEvent(	eventSource,
																		null);
		final Optional<DisplayEvent> optionalButtonNotifyEvent = Optional.of(buttonNotifyEvent);

		final Thread guiThread = new Thread() {
			@Override
			public void run() {
				// keep reference, else we garbage collect too fast which messes
				// up qt jambi.
				DummyQJRenderEngine dummyQJRenderEngine = new DummyQJRenderEngine();

				final DummyView view = new DummyView();

				final QJRenderEventConverter qjRenderEventConverter = mock(QJRenderEventConverter.class);
				final QMouseEvent mouseEnterEvent = new QMouseEvent(Type.Enter,
																	new QPoint(),
																	MouseButton.NoButton,
																	new MouseButtons(0));
				final QFocusEvent focusEvent = new QFocusEvent(Type.FocusIn);
				final QMouseEvent mouseClickEvent = new QMouseEvent(Type.MouseButtonPress,
																	new QPoint(),
																	MouseButton.LeftButton,
																	new MouseButtons(0));
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view,
																mouseClickEvent)).thenReturn(optionalButtonNotifyEvent);
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view,
																focusEvent)).thenReturn(optionalFocusGainNotifyEvent);

				new QJViewEventTracker(	displayEventBus,
										qjRenderEventConverter,
										eventSource,
										view);

				QApplication.sendEvent(	view.getPushButton0(),
										mouseEnterEvent);
				QApplication.sendEvent(	view,
										focusEvent);
				QApplication.sendEvent(	view,
										mouseClickEvent);

				QApplication.exec();
				dummyQJRenderEngine.dispose();
				dummyQJRenderEngine = null;
			}
		};
		guiThread.setDaemon(true);
		guiThread.start();
		Thread.sleep(1000);
		QApplication.quit();
		guiThread.join(2000);
		verify(	displayEventBus,
				times(1)).post(focusGainNotifyEvent);
		verifyNoMoreInteractions(displayEventBus);
	}

	@Test
	public void testInputEvents() throws InterruptedException {
		final EventBus displayEventBus = mock(EventBus.class);

		final DisplayEventTarget eventSource = mock(DisplayEventTarget.class);
		final DisplayEvent focusGainNotifyEvent = new FocusGainNotifyEvent(eventSource);
		final Optional<DisplayEvent> optionalFocusGainNotifyEvent = Optional.of(focusGainNotifyEvent);
		final DisplayEvent buttonNotifyEvent = new ButtonNotifyEvent(	eventSource,
																		null);
		final Optional<DisplayEvent> optionalButtonNotifyEvent = Optional.of(buttonNotifyEvent);

		final Thread guiThread = new Thread() {
			@Override
			public void run() {
				// keep reference, else we garbage collect too fast which messes
				// up qt jambi.
				DummyQJRenderEngine dummyQJRenderEngine = new DummyQJRenderEngine();

				final DummyView view = new DummyView();

				final QJRenderEventConverter qjRenderEventConverter = mock(QJRenderEventConverter.class);
				final QFocusEvent focusEvent = new QFocusEvent(Type.FocusIn);
				final QMouseEvent mouseEvent = new QMouseEvent(	Type.MouseButtonPress,
																new QPoint(),
																MouseButton.LeftButton,
																new MouseButtons(0));

				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view,
																mouseEvent)).thenReturn(optionalButtonNotifyEvent);
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view.getPushButton0(),
																mouseEvent)).thenReturn(optionalButtonNotifyEvent);
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view.getPushButton1(),
																mouseEvent)).thenReturn(optionalButtonNotifyEvent);
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																view,
																focusEvent)).thenReturn(optionalFocusGainNotifyEvent);

				new QJViewInputTracker(	displayEventBus,
										qjRenderEventConverter,
										eventSource,
										view);

				QApplication.sendEvent(	view,
										focusEvent);
				QApplication.sendEvent(	view,
										mouseEvent);
				QApplication.sendEvent(	view.getPushButton0(),
										mouseEvent);
				QApplication.sendEvent(	view.getPushButton1(),
										mouseEvent);

				final QWidget extraChild = new QWidget();
				when(qjRenderEventConverter.convertRenderEvent(	eventSource,
																view,
																extraChild,
																mouseEvent)).thenReturn(optionalButtonNotifyEvent);
				extraChild.setParent(view);

				QApplication.sendEvent(	extraChild,
										mouseEvent);

				QApplication.exec();
				dummyQJRenderEngine.dispose();
				dummyQJRenderEngine = null;
			}
		};
		guiThread.setDaemon(true);
		guiThread.start();
		Thread.sleep(1000);
		QApplication.quit();
		guiThread.join(2000);
		verify(	displayEventBus,
				times(4)).post(buttonNotifyEvent);
		verifyNoMoreInteractions(displayEventBus);
	}
}