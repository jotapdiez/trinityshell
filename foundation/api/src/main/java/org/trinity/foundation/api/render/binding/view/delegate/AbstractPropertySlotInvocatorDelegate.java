package org.trinity.foundation.api.render.binding.view.delegate;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 *
 */
public abstract class AbstractPropertySlotInvocatorDelegate implements PropertySlotInvocatorDelegate {
    @Override
    public ListenableFuture<Void> invoke(final Object view, final Method viewMethod, final Object argument) {
        final ListenableFutureTask<Void> invokeTask = ListenableFutureTask.create(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                viewMethod.invoke(view,
                        argument);
                return null;
            }
        });
        invokeSlot(invokeTask);

        return invokeTask;
    }

    protected abstract void invokeSlot(ListenableFutureTask<Void> invokeTask);
}