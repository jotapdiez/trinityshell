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
package org.trinity.render.paintengine.qt.api.painter;

import javax.inject.Named;

import org.trinity.core.render.api.Paintable;

import com.google.inject.assistedinject.Assisted;

/*****************************************
 * @author Erik De Rijcke
 ****************************************/
public interface QFPaintInstructionFactory {
	@Named("QFMove")
	QFPaintConstruction createMoveInstruction(	@Assisted("x") int x,
												@Assisted("y") int y);

	@Named("QFMoveResize")
	QFPaintInstruction createMoveResizeInstruction(	@Assisted("x") int x,
													@Assisted("y") int y,
													@Assisted("width") int width,
													@Assisted("height") int height);

	@Named("QFResize")
	QFPaintInstruction createResizeInstruction(	@Assisted("width") int width,
												@Assisted("height") int height);

	@Named("QFSetParent")
	QFPaintInstruction createSetParentInstruction(	@Assisted Paintable parent,
													@Assisted("x") int x,
													@Assisted("y") int y);
}