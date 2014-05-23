package org.trinity.wayland.defaul;

import javax.inject.Inject;

/**
 * Created by Erik De Rijcke on 5/22/14.
 */
public final class EagerSingletons {
    @Inject
    static WlDataDeviceManager wlDataDeviceManager;
    @Inject
    static WlShm wlShm;
    @Inject
    static WlShell wlShell;
    @Inject
    static WlCompositor wlCompositor;

    @Inject
    EagerSingletons() {
    }
}
