package org.trinity.shellplugin.wm.impl.view;

import org.trinity.foundation.api.render.PaintRenderer;
import org.trinity.shellplugin.widget.impl.view.qt.AbstractQWidgetViewProvider;

import com.google.inject.Inject;

public class ShellRootViewProvider extends AbstractQWidgetViewProvider<ShellRootView> {

	@Inject
	protected ShellRootViewProvider(final PaintRenderer paintRenderer) {
		super(paintRenderer);
	}

	@Override
	protected ShellRootView createView() {
		return new ShellRootView();
	}
}