/*
 * This file is part of Fusion-X11.
 * 
 * Fusion-X11 is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Fusion-X11 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Fusion-X11. If not, see <http://www.gnu.org/licenses/>.
 */
package org.fusion.x11.core.xcb;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.fusion.x11.core.DataContainer;
import org.fusion.x11.core.FlexDataContainer;
import org.fusion.x11.core.XAtom;
import org.fusion.x11.core.XCoreInterface;
import org.fusion.x11.core.XDisplay;
import org.fusion.x11.core.XDisplayPlatform;
import org.fusion.x11.core.XID;
import org.fusion.x11.core.XProtocolConstants;
import org.fusion.x11.core.XResourceHandle;
import org.fusion.x11.core.XWindow;
import org.fusion.x11.core.XWindowAttributes;
import org.fusion.x11.core.XWindowGeometry;
import org.fusion.x11.core.extension.XExtensions;
import org.fusion.x11.core.input.XKeySymbol;
import org.fusion.x11.core.input.XKeyboard;
import org.fusion.x11.core.input.XMouse;
import org.fusion.x11.core.xcb.error.NativeLibraryNotFoundException;
import org.fusion.x11.core.xcb.error.XcbNativeErrorHandler;
import org.fusion.x11.core.xcb.extension.XcbExtensions;
import org.fusion.x11.core.xcb.input.XcbKeyboard;
import org.fusion.x11.nativeHelpers.FusionNativeLibLoader;
import org.fusion.x11.nativeHelpers.NativeBufferHelper;
import org.fusion.x11.nativeHelpers.XNativeCaller;
import org.trinity.core.display.api.event.ClientMessageEvent;
import org.trinity.core.display.api.event.DisplayEvent;
import org.trinity.core.display.impl.EventPropagator;
import org.trinity.core.geometry.api.Coordinates;
import org.trinity.core.input.api.Button;
import org.trinity.core.input.api.InputModifiers;
import org.trinity.core.input.api.Key;
import org.trinity.core.input.impl.BaseKey;

/**
 * An <code>XcbCoreInterfaceImpl</code> provides access to the native X back-end
 * through the Xcb library.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public final class XcbCoreInterfaceImpl implements XCoreInterface {

	private static final Logger logger = Logger
			.getLogger(XcbCoreInterfaceImpl.class);
	private static final String NATIVE_LIB_NOT_FOUND_LOGMESSAGE = "Native library not found.";

	private final XcbCoreNativeCalls xcbCoreNativeCalls;
	private final XNativeCaller xNativeCaller;
	private final XDisplayPlatform xDisplayPlatform;

	/**
	 * 
	 * @param xDisplayPlatform
	 */
	public XcbCoreInterfaceImpl(final XDisplayPlatform xDisplayPlatform) {
		try {
			FusionNativeLibLoader.loadNativeFusionXcb();
		} catch (final IOException e) {

			XcbCoreInterfaceImpl.logger.fatal(
					XcbCoreInterfaceImpl.NATIVE_LIB_NOT_FOUND_LOGMESSAGE, e);

			throw new NativeLibraryNotFoundException(e,
					FusionNativeLibLoader.LIBNAME_FUSION_XCB);
		}

		this.xDisplayPlatform = xDisplayPlatform;
		this.xcbCoreNativeCalls = new XcbCoreNativeCalls();
		this.xNativeCaller = new XNativeCaller(new XcbNativeErrorHandler());
	}

	@Override
	public void forceDestroyWindow(final XWindow window) {
		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallDestroyWindow(), displayAddress,
				winId);

	}

	@Override
	public void enableEvents(final XWindow window,
			final EventPropagator... eventMasks) {

		long eventMask0 = 0L;
		for (final EventPropagator eventPropagator : eventMasks) {
			if (eventPropagator == EventPropagator.NOTIFY_CHANGED_WINDOW_PROPERTY) {
				eventMask0 |= XProtocolConstants.PROPERTY_CHANGE_MASK;
				continue;
			}
			if (eventPropagator == EventPropagator.NOTIFY_MOUSE_ENTER) {
				eventMask0 |= XProtocolConstants.ENTER_WINDOW_MASK;
				continue;
			}
			if (eventPropagator == EventPropagator.NOTIFY_MOUSE_LEAVE) {
				eventMask0 |= XProtocolConstants.LEAVE_WINDOW_MASK;
				continue;
			}
			if (eventPropagator == EventPropagator.NOTIFY_CHANGED_WINDOW_GEOMETRY) {
				eventMask0 |= XProtocolConstants.STRUCTURE_NOTIFY_MASK;
				continue;
			}
			if (eventPropagator == EventPropagator.REDIRECT_CHILD_WINDOW_GEOMTRY_CHANGES) {
				eventMask0 |= XProtocolConstants.SUBSTRUCTURE_REDIRECT_MASK;
				continue;
			}
			if (eventPropagator == EventPropagator.NOTIFY_CHANGED_WINDOW_FOCUS) {
				eventMask0 |= XProtocolConstants.FOCUS_CHANGE_MASK;
				continue;
			}
		}
		final Long eventMask = Long.valueOf(eventMask0);
		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallEnableEvents(), displayAddress,
				winId, eventMask);

	}

	@Override
	public void flush(final XDisplay display) {
		final Long displayAddress = display.getNativePeer();

		this.xNativeCaller.doNativeCall(this.xcbCoreNativeCalls.getCallFlush(),
				displayAddress);

	}

	@Override
	public void focusWindow(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallFocusWindow(), displayAddress,
				winId, time);

	}

	@Override
	public XWindow[] getChildWindows(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		Long[] windowIDs;

		windowIDs = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetChildWindows(),
				displayAddress, winId);

		final XWindow[] xWindows = new XWindow[windowIDs.length];

		// TODO move this for code block to a more common place where it can be
		// shared?
		for (int i = 0; i < windowIDs.length; i++) {
			final XID xid = new XID(window.getDisplayResourceHandle()
					.getDisplay(), XResourceHandle.valueOf(windowIDs[i]));
			xWindows[i] = this.xDisplayPlatform.getResourcesRegistry()
					.getClientXWindow(xid);
		}

		return xWindows;
	}

	@Override
	public DisplayEvent getNextEvent(final XDisplay display) {

		final Long displayAddress = display.getNativePeer();
		DisplayEvent displayEvent;

		displayEvent = makeEvent(this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetNextEvent(), displayAddress),
				display);

		return displayEvent;
	}

	@Override
	public XWindow getRootWindow(final XDisplay display) {

		final Long displayAddress = display.getNativePeer();
		Long windowID;

		windowID = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetRootWindow(), displayAddress);

		final XID xid = new XID(display, XResourceHandle.valueOf(windowID));

		final XWindow returnXWindow = this.xDisplayPlatform
				.getResourcesRegistry().getClientXWindow(xid);

		return returnXWindow;

	}

	@Override
	public XWindowAttributes getWindowAttributesCopy(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		XWindowAttributes wa;

		wa = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetWindowAttributesCopy(),
				displayAddress, winId);

		return wa;
	}

	@Override
	public XWindowGeometry getWindowGeometry(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		XWindowGeometry wg;

		wg = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetWindowGeometry(),
				displayAddress, winId);

		return wg;
	}

	@Override
	public void lowerWindow(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallLowerWindow(), displayAddress,
				winId);

	}

	/**
	 * 
	 * @param eventStruct
	 * @param display
	 * @return @
	 */
	private DisplayEvent makeEvent(final NativeBufferHelper eventStruct,
			final XDisplay display) {
		final DisplayEvent returnXcbEvent = XcbEventParser.parseEvent(
				eventStruct, display);
		return returnXcbEvent;
	}

	@Override
	public void mapWindow(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallMapWindow(), displayAddress,
				winId);

	}

	@Override
	public void moveResizeWindow(final XWindow window, final int x,
			final int y, final int width, final int height) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallMoveResizeWindow(),
				displayAddress, winId, Integer.valueOf(x), Integer.valueOf(y),
				Integer.valueOf(width), Integer.valueOf(height));

	}

	@Override
	public void moveWindow(final XWindow window, final int x, final int y) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallMoveWindow(), displayAddress,
				winId, Integer.valueOf(x), Integer.valueOf(y));

	}

	@Override
	public XDisplay openDisplay(final XDisplayPlatform displayPlatform,
			final String displayName, final int screenNr) {

		Long displayPeer;

		displayPeer = this.xNativeCaller
				.doNativeCall(this.xcbCoreNativeCalls.getCallOpenDisplay(),
						null, displayName);

		final XDisplay returnXDisplay = new XDisplay(displayName, screenNr,
				displayPlatform, displayPeer, this);

		return returnXDisplay;
	}

	@Override
	public void overrideRedirectWindow(final XWindow window,
			final boolean override) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallOverrideRedirectWindow(),
				displayAddress, winId, Boolean.valueOf(override));

	}

	@Override
	public void raiseWindow(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallRaiseWindow(), displayAddress,
				winId);

	}

	@Override
	public void reparentWindow(final XWindow parent, final XWindow child,
			final int newX, final int newY) {

		final Long displayAddress = parent.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long childWinId = child.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Long parentWinId = parent.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallReparentWindow(),
				displayAddress, childWinId, parentWinId, Integer.valueOf(newX),
				Integer.valueOf(newY));

	}

	@Override
	public void resizeWindow(final XWindow window, final int width,
			final int height) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallResizeWindow(), displayAddress,
				winId, Integer.valueOf(width), Integer.valueOf(height));

	}

	@Override
	public void shutDownDisplay(final XDisplay display) {

		final Long displayAddress = display.getNativePeer();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallShutdownDisplay(),
				displayAddress);

	}

	@Override
	public void unmapWindow(final XWindow window) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long winId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUnmapWindow(), displayAddress,
				winId);

	}

	@Override
	public void updateXMousePointer(final XMouse mousePointer) {

		final Long displayAddress = mousePointer.getDisplay().getNativePeer();

		// Contents of native buffer:
		// uint8_t response_type; /**< */
		// uint8_t same_screen; /**< */
		// uint16_t sequence; /**< */
		// uint32_t length; /**< */
		// xcb_window_t root; /**< */
		// xcb_window_t child; /**< */
		// int16_t root_x; /**< */
		// int16_t root_y; /**< */
		// int16_t win_x; /**< */
		// int16_t win_y; /**< */
		// uint16_t mask; /**< */
		// uint8_t pad0[2]; /**< */
		NativeBufferHelper pointerInfo;

		pointerInfo = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUpdateXMousePointer(),
				displayAddress);

		// final short responseType =
		pointerInfo.readUnsignedByte();
		// final short same_screen =
		pointerInfo.readUnsignedByte();
		// final int sequence =
		pointerInfo.readUnsignedShort();
		// final long length =
		pointerInfo.readUnsignedInt();
		// final long root =
		pointerInfo.readUnsignedInt();
		// final long child =
		pointerInfo.readUnsignedInt();
		final int rootX = pointerInfo.readSignedShort();
		final int rootY = pointerInfo.readSignedShort();
		// final short winX = (short)
		// pointerInfo.readSignedShort();
		// final short winY = (short)
		// pointerInfo.readSignedShort();
		// final int mask =
		// pointerInfo.readSignedShort();
		pointerInfo.enableWrite();

		// final XWindow relativeWindow = readXWindow(mousePointer.getDisplay(),
		// child, this);

		// mousePointer.setRelativePlatformRenderArea(relativeWindow);
		mousePointer.updateRootX(rootX);
		mousePointer.updateRootY(rootY);

	}

	@Override
	public long getXKeySymbol(final long keySymsPeer, final Key baseKey,
			final int keyColumn) {

		final Long keySymbol = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetKeySym(),
				// no display peer needed
				null, Long.valueOf(keySymsPeer),
				Integer.valueOf(baseKey.getKeyCode()),
				Integer.valueOf(keyColumn));
		return keySymbol.longValue();

	}

	@Override
	public XKeyboard initXKeyboard(final XDisplay display) {
		return new XcbKeyboard(display, this);
	}

	@Override
	public BaseKey[] getXKeyCodes(final long keySymsPeer,
			final XKeySymbol keySymbol) {

		final Integer[] keyCodes = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetKeyCodes(), null,
				Long.valueOf(keySymsPeer),
				Long.valueOf(keySymbol.getSymbolCode()));
		final BaseKey[] keys = new BaseKey[keyCodes.length];
		for (int i = 0; i < keyCodes.length; i++) {
			keys[i] = new BaseKey(keyCodes[i]);
		}
		return keys;

	}

	@Override
	public void grabKey(final XWindow window, final Key key,
			final InputModifiers inputModifiers) {
		final Long displayPeer = window.getDisplayResourceHandle().getDisplay()
				.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer keyCode = key.getKeyCode();
		final Integer inputModifiersMask = Integer.valueOf(inputModifiers
				.getInputModifiersMask());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGrabKey(), displayPeer,
				windowId, keyCode, inputModifiersMask);

	}

	@Override
	public void grabButton(final XWindow window, final Button button,
			final InputModifiers inputModifiers) {
		final Long displayPeer = window.getDisplayResourceHandle().getDisplay()
				.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer buttonCode = button.getButtonCode();
		final Integer inputModifiersMask = Integer.valueOf(inputModifiers
				.getInputModifiersMask());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGrabButton(), displayPeer,
				windowId, buttonCode, inputModifiersMask);

	}

	@Override
	public void ungrabKey(final XWindow window, final Key baseKey,
			final InputModifiers baseInputModifiers) {
		final Long displayPeer = window.getDisplayResourceHandle().getDisplay()
				.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer keyCode = baseKey.getKeyCode();
		final Integer inputModifiersMask = Integer.valueOf(baseInputModifiers
				.getInputModifiersMask());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUngrabKey(), displayPeer,
				windowId, keyCode, inputModifiersMask);

	}

	@Override
	public void ungrabButton(final XWindow window, final Button button,
			final InputModifiers inputModifiers) {
		final Long displayPeer = window.getDisplayResourceHandle().getDisplay()
				.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer buttonCode = button.getButtonCode();
		final Integer inputModifiersMask = Integer.valueOf(inputModifiers
				.getInputModifiersMask());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUngrabButton(), displayPeer,
				windowId, buttonCode, inputModifiersMask);

	}

	@Override
	public Long internAtom(final XDisplay display, final String atomName) {
		final Long displayAddress = display.getNativePeer();
		Long atomId;

		atomId = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallInternAtom(),
				displayAddress.longValue(), atomName);

		return atomId;
	}

	@Override
	public void setPropertyInstance(final XWindow window, final XAtom property,
			final XAtom propertyInstanceType,
			final DataContainer<?> propertyValue) {

		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Long propertyAtomId = property.getAtomId();
		final Long typeAtomId = propertyInstanceType.getAtomId();
		final Integer format = Integer.valueOf(propertyValue.getDataFormat()
				.getFormat());
		final byte[] data = propertyValue.getAllData();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallChangeProperty(),
				displayAddress, windowId, propertyAtomId, typeAtomId, format,
				data);

	}

	@Override
	public FlexDataContainer getPropertyValue(final XWindow window,
			final XAtom property) {
		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Long propertyAtomId = property.getAtomId();

		FlexDataContainer returnrawPropertyValueContainerContainer;

		final NativeBufferHelper rawPropertyValueContainer = this.xNativeCaller
				.doNativeCall(this.xcbCoreNativeCalls.getCallGetProperty(),
						displayAddress, windowId, propertyAtomId);

		final byte[] rawContents = rawPropertyValueContainer.getAllData();
		rawPropertyValueContainer.enableWrite();
		returnrawPropertyValueContainerContainer = new FlexDataContainer(
				ByteBuffer.wrap(rawContents));

		return returnrawPropertyValueContainerContainer;

	}

	@Override
	public void sendClientMessage(final XWindow window,
			final ClientMessageEvent clientMessageEvent) {
		final Long displayAddress = window.getDisplayResourceHandle()
				.getDisplay().getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Long atomId = clientMessageEvent.getMessageType().getAtomId();
		final Integer format = Integer.valueOf(clientMessageEvent
				.getDataFormat());
		final byte[] data = clientMessageEvent.getData();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallSendClientMessage(),
				displayAddress, windowId, atomId, format, data);

	}

	@Override
	public void grabKeyboard(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGrabKeyboard(), displayAddress,
				windowId, time);

	}

	@Override
	public void grabMouse(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGrabMouse(), displayAddress,
				windowId, time);

	}

	// TODO change argument to XDisplay
	@Override
	public void ungrabKeybard(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUngrabKeyboard(),
				displayAddress, time);

	}

	// TODO change argument to XDisplay
	@Override
	public void ungrabMouse(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallUngrabMouse(), displayAddress,
				time);

	}

	@Override
	public XWindow getInputFocus(final XDisplay display) {
		final Long displayAddress = display.getNativePeer();

		final Long inputWindowId = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallGetInputFocus(), displayAddress);
		final XWindow inputWindow = display
				.getDisplayPlatform()
				.getResourcesRegistry()
				.getClientXWindow(
						new XID(display, XResourceHandle.valueOf(inputWindowId)));

		return inputWindow;
	}

	@Override
	public XExtensions initXExtensions(final XDisplay display) {
		return new XcbExtensions(display, this.xNativeCaller);
	}

	@Override
	public XMouse initXMouse(final XDisplay display) {
		return new XMouse(display, this);
	}

	@Override
	public XWindow createNewWindow(final XDisplay display) {
		final Long displayAddress = display.getNativePeer();
		final Long windowId = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallCreateNewWindow(),
				displayAddress);
		final XWindow newWindow = display
				.getDisplayPlatform()
				.getResourcesRegistry()
				.getClientXWindow(
						new XID(display, XResourceHandle.valueOf(windowId)));
		return newWindow;
	}

	@Override
	public void setSelectionOwner(final XAtom selectionAtom, final XWindow owner) {

		final XDisplay display = owner.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long selectionAtomId = selectionAtom.getAtomId();
		final Long windowId = owner.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer time = Integer.valueOf(display.getLastServerTime());

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallSetSelectionOwner(),
				displayAddress, selectionAtomId, windowId, time);
	}

	@Override
	public XWindow getSelectionOwner(final XAtom selectionAtom) {
		final XDisplay display = selectionAtom.getDisplay();

		final Long displayAddress = display.getNativePeer();
		final Long atomId = selectionAtom.getAtomId();

		final Long ownerWindowId = this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallSelectionOwner(),
				displayAddress, atomId);

		final XWindow ownerWindow = display
				.getDisplayPlatform()
				.getResourcesRegistry()
				.getClientXWindow(
						new XID(display, XResourceHandle.valueOf(ownerWindowId)));

		return ownerWindow;
	}

	@Override
	public void addToSaveSet(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallAddToSaveSet(), displayAddress,
				windowId);
	}

	@Override
	public void removeFromSaveSet(final XWindow window) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();

		this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallRemoveFromSaveSet(),
				displayAddress, windowId);
	}

	@Override
	public Coordinates translateCoordinates(final XWindow window,
			final XWindow source, final int sourceX, final int sourceY) {
		final XDisplay display = window.getDisplayResourceHandle().getDisplay();
		final Long displayAddress = display.getNativePeer();
		final Long windowId = window.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Long sourceWindowId = source.getDisplayResourceHandle()
				.getResourceHandle().getNativeHandle();
		final Integer sourceXCoordinate = Integer.valueOf(sourceX);
		final Integer sourceYCoordinate = Integer.valueOf(sourceY);

		return this.xNativeCaller.doNativeCall(
				this.xcbCoreNativeCalls.getCallTranslateCoordinates(),
				displayAddress, windowId, sourceWindowId, sourceXCoordinate,
				sourceYCoordinate);
	}
}
