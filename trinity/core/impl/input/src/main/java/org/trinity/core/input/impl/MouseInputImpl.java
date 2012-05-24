/*
 * This file is part of Hydrogen. Hydrogen is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. Hydrogen is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with Hydrogen. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.trinity.core.input.impl;

import org.trinity.core.input.api.Momentum;
import org.trinity.core.input.api.MouseInput;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

// TODO documentation
/**
 * <code>MouseInput</code> is user {@link InputImpl} that originated from a
 * mouse.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public class MouseInputImpl extends InputImpl implements MouseInput {

	private final ButtonImpl baseButton;
	private final InputModifiersImpl baseInputModifiers;

	private final int rootX;
	private final int rootY;
	private final int relativeX;
	private final int relativeY;

	/**
	 * @param momentum
	 * @param baseButton
	 * @param baseInputModifiers
	 * @param rootX
	 * @param rootY
	 * @param relativeX
	 * @param relativeY
	 */
	@Inject
	protected MouseInputImpl(	final Momentum momentum,
								@Assisted final ButtonImpl baseButton,
								@Assisted final InputModifiersImpl baseInputModifiers,
								@Assisted("rootX") final int rootX,
								@Assisted("rootY") final int rootY,
								@Assisted("relativeX") final int relativeX,
								@Assisted("relativeY") final int relativeY) {
		super(momentum);
		this.baseButton = baseButton;
		this.baseInputModifiers = baseInputModifiers;
		this.rootX = rootX;
		this.rootY = rootY;
		this.relativeX = relativeX;
		this.relativeY = relativeY;
	}

	/**
	 * @return
	 */
	@Override
	public ButtonImpl getButton() {
		return this.baseButton;
	}

	/**
	 * @return
	 */
	@Override
	public InputModifiersImpl getModifiers() {
		return this.baseInputModifiers;
	}

	/**
	 * @return
	 */
	@Override
	public int getRelativeX() {
		return this.relativeX;
	}

	/**
	 * @return
	 */
	@Override
	public int getRelativeY() {
		return this.relativeY;
	}

	/**
	 * @return
	 */
	@Override
	public int getRootX() {
		return this.rootX;
	}

	/**
	 * @return
	 */
	@Override
	public int getRootY() {
		return this.rootY;
	}
}
