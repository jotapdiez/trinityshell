package org.trinity.foundation.api.render.binding;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.trinity.foundation.api.render.binding.model.PropertyChanged;
import org.trinity.foundation.api.render.binding.view.PropertySlot;

import com.google.inject.Inject;

/***************************************
 * Post processes a method annotated with {@link PropertyChanged}. It reads the
 * {@link PropertyChanged#value()} and finds for each String value a
 * {@link ViewProperty} and {@link PropertySlot} with the same name. Each
 * matching {@code ViewProperty} will be read and it's corresponding
 * {@code PropertySlot} will be invoked. Invoking the {@code PropertySlot} is
 * done through an underlying {@link ViewSlotHandler} implementation.
 * <p>
 * This class is used by Google Guice AOP.
 *************************************** 
 */
public class PropertyChangedSignalDispatcher implements MethodInterceptor {

	private NewBinder binder;

	@Override
	public Object invoke(final MethodInvocation invocation) throws Throwable {
		final Object invocationResult = invocation.proceed();

		final Object changedModel = invocation.getThis();

		final PropertyChanged changedPropertySignal = invocation.getMethod().getAnnotation(PropertyChanged.class);
		final String[] changedPropertyNames = changedPropertySignal.value();

		for (final String propertyName : changedPropertyNames) {
			getBinder().updateView(	changedModel,
									propertyName);
		}

		return invocationResult;
	}

	public NewBinder getBinder() {
		return this.binder;
	}

	@Inject
	void setBinder(final NewBinder binder) {
		this.binder = binder;
	}
}