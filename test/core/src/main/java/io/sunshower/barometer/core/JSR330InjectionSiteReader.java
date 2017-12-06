package io.sunshower.barometer.core;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Registry;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by haswell on 3/24/16.
 */
public class JSR330InjectionSiteReader implements InjectionSiteReader {

    @Override
    public Set<InjectionSite<?, ?>> read(Class<?> type, Object instance, Registry r) {
        final Set<InjectionSite<?, ?>> injectionSites = new HashSet<>();
        collectFieldInjectionSites(type, instance, r, injectionSites);
        return injectionSites;
    }

    private void collectFieldInjectionSites(
            Class<?> type,
            Object instance,
            Registry r,
            Set<InjectionSite<?, ?>> injectionSites
    ) {
        injectionSites.addAll(Reflect.collectOverHierarchy(type,
                (t) -> Arrays.stream(t.getDeclaredFields()).filter(this::isInjectable))
                .map(f -> createInjectionSite(type, r, f))
                .collect(Collectors.toSet()));
    }

    private InjectionSite<?, ?> createInjectionSite(Class<?> type, Registry r, Field f) {
        final String name = resolveName(f);
        return new FieldInjectionSite<>(name, type, r, f);
    }

    private String resolveName(Field f) {
        final Named nameAnnotation = f.getAnnotation(Named.class);
        if(nameAnnotation != null) {
            return nameAnnotation.value();
        }
        final Resource resourceAnnotation = f.getAnnotation(Resource.class);
        if(resourceAnnotation != null) {
            return resourceAnnotation.name();
        }
        return f.getName();
    }

    private boolean isInjectable(Field f) {
        return f.isAnnotationPresent(Named.class) ||
                f.isAnnotationPresent(Resource.class) ||
                f.isAnnotationPresent(Decorator.class) ||
                f.isAnnotationPresent(Inject.class);
    }
}
