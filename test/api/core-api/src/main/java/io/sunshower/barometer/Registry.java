package io.sunshower.barometer;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Created by haswell on 3/23/16.
 * Barometer implementations should implement this class.
 */
public interface Registry {

    ModuleReader getModuleReader();

    <T> Optional<T> find(String name);

    <T> Optional<T> find(Class<T> type);

    Set<Class<?>> getModules();

    <T> Optional<T> find(Class<T> type, String name);

    <T> T getModule(Class<T> type);

    <T extends Annotation> T getAnnotation(Class<T> type);

    <T, U extends Annotation> T getHostModule(Class<U> type);

    /**
     * Optional
     * @return the span of this unit
     */
    default Set<Class<?>> getSpan() {
        return Collections.emptySet();
    }
}
