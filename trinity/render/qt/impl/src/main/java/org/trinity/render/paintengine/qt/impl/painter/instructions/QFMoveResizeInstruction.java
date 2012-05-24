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
package org.trinity.render.paintengine.qt.impl.painter.instructions;

import org.trinity.core.render.api.Paintable;
import org.trinity.render.paintengine.qt.api.QFRenderEngine;
import org.trinity.render.paintengine.qt.api.painter.QFPaintInstruction;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/*****************************************
 * @author Erik De Rijcke
 ****************************************/
public class QFMoveResizeInstruction implements QFPaintInstruction {

	private final int x;
	private final int y;
	private final int width;
	private final int height;

	@Inject
	protected QFMoveResizeInstruction(	@Assisted("x") final int x,
										@Assisted("y") final int y,
										@Assisted("width") final int width,
										@Assisted("height") final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.hydrogen.paint.api.PaintInstruction#call(org.hydrogen.paint.api.Paintable
	 * , org.hydrogen.paint.api.RenderEngine)
	 */
	@Override
	public void call(	final Paintable paintable,
						final QFRenderEngine renderEngine) {
		renderEngine.getVisual(paintable).setGeometry(	this.x,
														this.y,
														this.width,
														this.height);
	}
}
