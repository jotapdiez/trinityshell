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

package org.fusion.x11.icccm;

import org.fusion.x11.error.NotYetImplementedError;
import org.trinity.display.x11.api.XProtocolConstants;
import org.trinity.display.x11.impl.FlexDataContainer;
import org.trinity.display.x11.impl.XServerImpl;
import org.trinity.display.x11.impl.XIDImpl;
import org.trinity.display.x11.impl.XResourceHandleImpl;
import org.trinity.display.x11.impl.XWindowImpl;
import org.trinity.display.x11.impl.property.XPropertyInstanceInfo;
import org.trinity.display.x11.impl.property.AbstractXProperty;
import org.trinity.foundation.display.api.PlatformRenderArea;

// TODO documentation
/**
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public final class WmHints extends AbstractXProperty<WmHintsInstance> {

	/**
	 * 
	 * @param atomId
	 * @param xAtomRegistry
	 */
	public WmHints(final XServerImpl display) {
		super(display, IcccmAtoms.WM_HINTS_ATOM_NAME, Long
				.valueOf(XProtocolConstants.WM_HINTS));
	}

	@Override
	public WmHintsInstance getPropertyInstance(
			final PlatformRenderArea platformRenderArea,
			final XPropertyInstanceInfo propertyInstanceInfo,
			final FlexDataContainer propertyDataContainer) {
		// contents of buffer:
		// /** Marks which fields in this structure are defined */
		// int32_t flags;
		// /** Does this application rely on the window manager to get
		// keyboard
		// input? */
		// uint32_t input;
		// /** See below */
		// int32_t initial_state;
		// /** Pixmap to be used as icon */
		// xcb_pixmap_t icon_pixmap;
		// /** Window to be used as icon */
		// xcb_window_t icon_window;
		// /** Initial position of icon */
		// int32_t icon_x, icon_y;
		// /** Icon mask bitmap */
		// xcb_pixmap_t icon_mask;
		// /* Identifier of related window group */
		// xcb_window_t window_group;

		final int flags = propertyDataContainer.readSignedInt();//
		final long input = propertyDataContainer.readUnsignedInt();//
		final int initialState = propertyDataContainer.readSignedInt();//
		final long iconPixmapId = propertyDataContainer.readUnsignedInt();//
		final long iconWindowId = propertyDataContainer.readUnsignedInt();//
		final int iconX = propertyDataContainer.readSignedInt();//
		final int iconY = propertyDataContainer.readSignedInt();//
		final long iconPixmapMaskId = propertyDataContainer.readUnsignedInt();//

		WmStateEnum initialStateEnum = WmStateEnum.NormalState;
		for (final WmStateEnum state : WmStateEnum.values()) {
			if (state.ordinal() == initialState) {
				initialStateEnum = state;
				break;
			}
		}
		final XIDImpl iconPixmapXid = new XIDImpl(getDisplay(),
				XResourceHandleImpl.valueOf(Long.valueOf(iconPixmapId)));
		final XIDImpl iconPixmapMaskXid = new XIDImpl(getDisplay(),
				XResourceHandleImpl.valueOf(Long.valueOf(iconPixmapMaskId)));
		final XWindowImpl iconWindow = getDisplay()
				.getDisplayPlatform()
				.getResourcesRegistry()
				.getClientXWindow(
						new XIDImpl(getDisplay(), XResourceHandleImpl.valueOf(Long
								.valueOf(iconWindowId))));

		return new WmHintsInstance(getDisplay(), flags, input,
				initialStateEnum, iconPixmapXid, iconWindow, iconX, iconY,
				iconPixmapMaskXid, null);
	}

	@Override
	public void setPropertyInstance(
			final PlatformRenderArea platformRenderArea,
			final WmHintsInstance propertyInstance) {
		throw new NotYetImplementedError("Not implemented");
	}
}
