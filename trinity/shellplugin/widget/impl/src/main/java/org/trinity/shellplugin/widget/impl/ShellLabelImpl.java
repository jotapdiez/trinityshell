/*
 * This file is part of HyperDrive. HyperDrive is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. HyperDrive is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with HyperDrive. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.trinity.shellplugin.widget.impl;

import org.trinity.foundation.render.api.PainterFactory;
import org.trinity.shell.api.node.ShellNodeExecutor;
import org.trinity.shell.api.surface.ShellDisplayEventDispatcher;
import org.trinity.shell.api.widget.BaseShellWidget;
import org.trinity.shell.api.widget.ShellWidgetView;
import org.trinity.shellplugin.widget.api.ShellLabel;
import org.trinity.shellplugin.widget.api.binding.ViewAttribute;
import org.trinity.shellplugin.widget.api.binding.ViewAttributeChanged;
import org.trinity.shellplugin.widget.api.binding.ViewReference;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.devsurf.injection.guice.annotations.Bind;

// TODO documentation
/**
 * @author Erik De Rijcke
 * @since 1.0
 */
@Bind
public class ShellLabelImpl extends BaseShellWidget implements ShellLabel {

	@ViewReference
	private final ShellWidgetView view;

	@ViewAttribute(name = "text")
	private String labelText;

	@Inject
	protected ShellLabelImpl(	final EventBus eventBus,
								final ShellDisplayEventDispatcher shellDisplayEventDispatcher,
								final PainterFactory painterFactory,
								@Named("shellWidgetGeoExecutor") final ShellNodeExecutor shellNodeExecutor,
								@Named("ShellLabelView") final ShellWidgetView view) {
		super(	eventBus,
				shellDisplayEventDispatcher,
				painterFactory,
				shellNodeExecutor,
				view);
		this.view = view;
	}

	@Override
	@ViewAttributeChanged("text")
	public void setText(final String text) {
		this.labelText = text;
	}

	@Override
	public String getText() {
		return this.labelText;
	}
}