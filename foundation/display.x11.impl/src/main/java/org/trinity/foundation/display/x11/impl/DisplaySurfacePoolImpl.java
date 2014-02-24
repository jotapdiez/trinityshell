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
package org.trinity.foundation.display.x11.impl;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trinity.foundation.api.display.DisplaySurface;
import org.trinity.foundation.api.display.DisplaySurfaceFactory;
import org.trinity.foundation.api.display.DisplaySurfaceHandle;
import org.trinity.foundation.api.display.DisplaySurfacePool;
import org.trinity.foundation.api.display.DisplaySurfaceReferencer;
import org.trinity.foundation.api.display.event.DestroyNotify;
import org.trinity.foundation.display.x11.api.XConnection;

import javax.annotation.concurrent.NotThreadSafe;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
@NotThreadSafe
public class DisplaySurfacePoolImpl implements DisplaySurfacePool {

    private static final Logger LOG = LoggerFactory.getLogger(DisplaySurfacePoolImpl.class);
    public final Map<Integer, DisplaySurface> displaySurfaces = new HashMap<>();
    private final XConnection xConnection;
    private final DisplaySurfaceFactory displaySurfaceFactory;

    @Inject
    DisplaySurfacePoolImpl(final XConnection xConnection,
                           final DisplaySurfaceFactory displaySurfaceFactory) {
        this.xConnection = xConnection;
        this.displaySurfaceFactory = displaySurfaceFactory;
    }

    @Override
    public DisplaySurface getDisplaySurface(final DisplaySurfaceHandle displaySurfaceHandle) {

        final int windowHash = displaySurfaceHandle.getNativeHandle().hashCode();
        DisplaySurface window = this.displaySurfaces.get(windowHash);
        if (window == null) {
            LOG.debug(  "Xwindow={} added to cache.",
                        displaySurfaceHandle);

            window = this.displaySurfaceFactory.construct(displaySurfaceHandle);
            window.register(new DestroyListener(window));
            this.displaySurfaces.put(windowHash,
                    window);
        }
        return window;
    }

    public boolean isPresent(final DisplaySurfaceHandle displaySurfaceHandle) {

        return this.displaySurfaces.containsKey(displaySurfaceHandle.getNativeHandle().hashCode());
    }

    @Override
    public DisplaySurfaceReferencer getDisplaySurfaceCreator() {
        this.xConnection.stop();

        return new DisplaySurfaceReferencer() {
            @Override
            public DisplaySurface reference(final DisplaySurfaceHandle displaySurfaceHandle) {
                return getDisplaySurface(displaySurfaceHandle);
            }

            @Override
            public void close() {
                DisplaySurfacePoolImpl.this.xConnection.start();
            }
        };
    }

    private class DestroyListener {
        private final DisplaySurface window;

        public DestroyListener(final DisplaySurface window) {
            this.window = window;
        }

        @Subscribe
        public void destroyed(final DestroyNotify destroyNotify) {

            DisplaySurfacePoolImpl.this.displaySurfaces.remove(this.window.getDisplaySurfaceHandle().getNativeHandle().hashCode());
            this.window.unregister(this);
        }
    }
}