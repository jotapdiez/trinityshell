/*
 * This file is part of HyperDrive.
 * 
 * HyperDrive is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * HyperDrive is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * HyperDrive. If not, see <http://www.gnu.org/licenses/>.
 */

package org.hyperdrive.input.api.base;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.hyperdrive.foundation.api.ManagedDisplay;
import org.hyperdrive.foundation.api.event.KeyboardKeyPressedHandler;
import org.hyperdrive.foundation.api.event.KeyboardKeyReleasedHandler;
import org.trinity.core.display.api.event.KeyNotifyEvent;
import org.trinity.core.input.api.InputModifierName;
import org.trinity.core.input.api.InputModifiers;
import org.trinity.core.input.api.Key;
import org.trinity.core.input.api.Modifier;
import org.trinity.core.input.api.Momentum;
import org.trinity.core.input.api.SpecialKeyName;
import org.trinity.core.input.impl.BaseInputModifiers;
import org.trinity.core.input.impl.BaseKey;

// TODO documentation
/**
 * A <code>KeyBinding</code> binds a {@link BaseKey} with an optional
 * {@link Modifier} to an action. A <code>Key</code> is identified by it's name.
 * <code>Key</code> names are the same as the unmodified character they produce
 * when pressed. A list of special <code>Key</code> names can be found in
 * {@link SpecialKeyName} . A list of <code>Modifier</code>s can be found in
 * {@link InputModifierName}.
 * 
 * @author Erik De Rijcke
 * @since 1.0
 */
public abstract class BaseKeyBinding {

	// Modifiers who's state should be ignored when checking for a key
	// binding.
	// TODO capslock, scrolllock, ...
	private static final InputModifierName[] IGNORED_MODIFIERS = new InputModifierName[] {
			InputModifierName.MOD_LOCK, InputModifierName.MOD_2 };

	// TODO release keybinding function

	private final ManagedDisplay managedDisplay;
	private final String keyName;
	private final InputModifierName[] modKeyNames;
	private final Momentum momentum;

	/**
	 * 
	 * @param sourceWindow
	 * @param momentum
	 * @param keyName
	 * @param ignoreOftenUsedModifiers
	 * @param modKeyNames
	 * 
	 */
	public BaseKeyBinding(final ManagedDisplay display,
			final Momentum momentum, final String keyName,
			final boolean ignoreOftenUsedModifiers,
			final InputModifierName... modKeyNames) {
		this.managedDisplay = display;
		this.keyName = keyName;
		this.modKeyNames = modKeyNames;
		this.momentum = momentum;

		installKeyBindingListener(ignoreOftenUsedModifiers);
	}

	/**
	 * 
	 * @param sourceWindow
	 * @param momentum
	 * @param keyName
	 * @param modKeyNames
	 * 
	 */
	public BaseKeyBinding(final ManagedDisplay display,
			final Momentum momentum, final String keyName,
			final InputModifierName... modKeyNames) {
		this(display, momentum, keyName, true, modKeyNames);
	}

	/**
	 * 
	 * @param sourceWindow
	 * @param momentum
	 * @param keyName
	 * @param modKeyNames
	 * 
	 */
	public BaseKeyBinding(final ManagedDisplay display,
			final Momentum momentum, final String keyName,
			final List<InputModifierName> modKeyNames) {
		this(display, momentum, keyName, modKeyNames
				.toArray(new InputModifierName[modKeyNames.size()]));
	}

	/**
	 * 
	 * @param ignoreOftenUsedModifiers
	 * 
	 */
	protected void installKeyBindingListener(
			final boolean ignoreOftenUsedModifiers) {
		// translate keyname and modkeynames to keys and modmask

		final Key[] validKeys = this.managedDisplay.getDisplay().getKeyBoard()
				.keys(getKeyName());

		final List<InputModifiers> validInputModifiersCombinations = new LinkedList<InputModifiers>();

		final InputModifiers inputModifiers = getModKeyNames().length == 0 ? new BaseInputModifiers()
				: this.managedDisplay.getDisplay().getKeyBoard()
						.modifiers(getModKeyNames());
		if (ignoreOftenUsedModifiers) {
			// install keybindings with variations on often active modifiers

			// This will only install an extra keybinding on an invidual
			// item from the IGNORED_MODIFIERS array.
			// TODO full permutation
			for (final InputModifierName ignoredModifier : BaseKeyBinding.IGNORED_MODIFIERS) {
				final Modifier modifier = this.managedDisplay.getDisplay()
						.getKeyBoard().modifier(ignoredModifier);

				final int modifierMaskWithExtraModifier = inputModifiers
						.getInputModifiersMask() & modifier.getModifierMask();
				final BaseInputModifiers inputModifiersWithExtraModifier = new BaseInputModifiers(
						modifierMaskWithExtraModifier);
				validInputModifiersCombinations
						.add(inputModifiersWithExtraModifier);
			}

		}
		validInputModifiersCombinations.add(inputModifiers);

		for (final Key validKey : validKeys) {
			// install a keygrab
			getManagedDisplay().getRoot().getPlatformRenderArea()
					.catchKeyboardInput(validKey, inputModifiers);

			if (getMomentum() == Momentum.STARTED) {
				getManagedDisplay().addDisplayEventHandler(
						new KeyboardKeyPressedHandler() {
							@Override
							public void handleEvent(final KeyNotifyEvent event) {
								validateAction(inputModifiers, validKey, event);
							}
						});
			} else {
				getManagedDisplay().addDisplayEventHandler(
						new KeyboardKeyReleasedHandler() {
							@Override
							public void handleEvent(final KeyNotifyEvent event) {
								validateAction(inputModifiers, validKey, event);
							}
						});
			}

			// getManagedDisplay()
			// .addEventHandler(
			// new EventHandler<KeyNotifyEvent>() {
			// @Override
			// public void handleEvent(
			// final KeyNotifyEvent event) {
			//
			// }
			// },
			// getMomentum() == Momentum.STARTED ? DisplayEventType.KEY_PRESSED
			// : DisplayEventType.KEY_RELEASED, 0);

		}
	}

	private void validateAction(final InputModifiers inputModifiers,
			final Key validKey, final KeyNotifyEvent event) {
		final short eventKeyCode = (short) event.getInput().getKey()
				.getKeyCode();
		final short validKeyCode = (short) validKey.getKeyCode();
		final boolean gotValidKey = eventKeyCode == validKeyCode;

		final int eventModifiersMask = event.getInput().getModifiers()
				.getInputModifiersMask();
		final int validEventModifiersMask = inputModifiers
				.getInputModifiersMask();

		final boolean gotValidModifiers = eventModifiersMask == validEventModifiersMask;

		final Momentum eventMomentum = event.getInput().getMomentum();
		final Momentum validMomentum = BaseKeyBinding.this.getMomentum();
		final boolean gotValidMomentum = eventMomentum == validMomentum;

		if (gotValidKey && gotValidModifiers && gotValidMomentum) {
			BaseKeyBinding.this.action();
		}
	}

	/**
	 * 
	 * @return
	 */
	public ManagedDisplay getManagedDisplay() {
		return this.managedDisplay;
	}

	/**
	 * 
	 * @return
	 */
	public String getKeyName() {
		return this.keyName;
	}

	/**
	 * 
	 * @return
	 */
	public InputModifierName[] getModKeyNames() {
		// return a copy so manipulation of the returned instance can take
		// place without interfering with the source.
		return Arrays.copyOf(this.modKeyNames, this.modKeyNames.length);
	}

	/**
	 * 
	 * @return
	 */
	public Momentum getMomentum() {
		return this.momentum;
	}

	/**
	 * 
	 */
	public abstract void action();
}
