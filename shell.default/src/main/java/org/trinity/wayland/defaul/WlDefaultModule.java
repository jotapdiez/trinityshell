package org.trinity.wayland.defaul;

import com.google.common.util.concurrent.Service;
import dagger.Module;
import dagger.Provides;
import jnr.ffi.LibraryLoader;
import org.freedesktop.wayland.server.Display;
import org.trinity.wayland.defaul.protocol.WlProtocolModule;

import javax.inject.Singleton;

import static dagger.Provides.Type.SET;

/**
 * Created by Erik De Rijcke on 5/26/14.
 */
@Module(
        includes = {
                WlProtocolModule.class
        },
        library = true
)
public class WlDefaultModule {
    @Singleton
    @Provides
    LibC provideLibC(){
        return LibraryLoader.create(LibC.class)
                            .load("c");
    }

    @Singleton
    @Provides
    WlJobExecutor provideWlJobExecutor(final Display display,
                                       final LibC    libC){
        return new WlJobExecutor(display,
                                 libC);
    }

    @Provides(type = SET)
    Service provideService(final WlShellService wlShellService) {
        return wlShellService;
    }
}
