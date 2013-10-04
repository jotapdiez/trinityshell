/*******************************************************************************
 * Trinity Shell Copyright (C) 2011 Erik De Rijcke
 *
 * This file is part of Trinity Shell.
 *
 * Trinity Shell is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * Trinity Shell is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 ******************************************************************************/

package org.trinity.foundation.render.javafx.api;

import com.cathive.fx.guice.fxml.FXMLLoadingModule;
import com.cathive.fx.guice.prefs.PersistentPropertyModule;
import com.cathive.fx.guice.thread.FxApplicationThreadModule;
import com.google.inject.AbstractModule;

public abstract class AbstractFXModule extends AbstractModule {

    private final Runnable applicationStartTask;

    protected AbstractFXModule(final Runnable applicationStartTask) {
        this.applicationStartTask = applicationStartTask;
    }

    @Override
    protected void configure() {

        install(new FXMLLoadingModule());
        install(new FxApplicationThreadModule());
        install(new PersistentPropertyModule());

        new Thread(applicationStartTask,
                   "JFX Application").start();
        try {
            final AbstractFXApplication application = AbstractFXApplication.GET();
            requestInjection(application);
            bind((Class<AbstractFXApplication>) application.getClass()).toInstance(application);
        } catch(InterruptedException e) {
            throw new RuntimeException(e);
        }

        fxConfigure();
    }

    protected abstract void fxConfigure();
}