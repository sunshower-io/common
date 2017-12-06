package io.sunshower.barometer.core;


public interface ContextInjector {

    <T> T inject(T instance);

    <T> T inject(Class<T> clazz);



}
