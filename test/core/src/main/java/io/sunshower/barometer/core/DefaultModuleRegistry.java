package io.sunshower.barometer.core;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Exports;
import io.sunshower.barometer.Module;
import io.sunshower.barometer.ModuleReader;
import io.sunshower.barometer.Registry;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static io.sunshower.arcus.reflect.Reflect.instantiate;
import static java.lang.String.format;

/**
 * Created by haswell on 3/23/16.
 */
public class DefaultModuleRegistry implements Registry, ContextInjector {


    private final Class<?> host;

    private final Set<Object> modules;

    private final ModuleReader<Class<?>> moduleReader;
    private final InjectionSiteReader injectionSiteReader;
    private final Set<Class<?>> moduleTypes;
    private final Map<Class<? extends Annotation>, Object> moduleAnnotations;

    public DefaultModuleRegistry(
            Class<?> host,
            ModuleReader<Class<?>> moduleReader,
            InjectionSiteReader injectionSiteReader
    ) {
        this.host = host;
        this.moduleReader = moduleReader;
        this.moduleAnnotations = new HashMap<>();
        this.injectionSiteReader = injectionSiteReader;
        this.moduleTypes = (Set<Class<?>>) moduleReader.read(host);
        this.modules = moduleTypes.stream()
                .map(Reflect::instantiate)
                .collect(Collectors.toCollection(HashSet::new));
    }

    public DefaultModuleRegistry(Class<?> host, ModuleReader<Class<?>> moduleReader) {
        this(host, moduleReader, new JSR330InjectionSiteReader());
    }

    public DefaultModuleRegistry(Class<?> host) {
        this(host, new AnnotationModuleReader(), new JSR330InjectionSiteReader());
    }

    @Override
    public <T> T inject(T instance) {
        return null;
    }

    @Override
    public <T> T inject(Class<T> clazz) {
        return find(clazz).orElse(createAndInject(clazz));
    }


    @Override
    public ModuleReader getModuleReader() {
        return moduleReader;
    }

    @Override
    public <T> Optional<T> find(String name) {
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> type) {
        if(Module.class.isAssignableFrom(type)) {
            return resolveAndInjectModule(type);
        }
        if(Annotation.class.isAssignableFrom(type)) {
            return resolveAnnotation((Class<Annotation>)type);
        }

        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> resolveAnnotation(Class<? extends Annotation> type) {
        for(Class<?> module : moduleTypes) {
            if(module.isAnnotationPresent(type)) {
                return (Optional<T>) Optional.of(module.getAnnotation(type));
            }
            final Object m = getModule(module);
        }
        return Optional.empty();
    }


    @Override
    public Set<Class<?>> getModules() {
        return Collections.unmodifiableSet(moduleTypes);
    }

    @Override
    public <T> Optional<T> find(Class<T> type, String name) {
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getModule(Class<T> type) {
        if(moduleTypes.contains(type)) {
            final Object result = instantiate(type);
            injectModule(result, type);
            return (T) result;
        }
        throw new NoSuchElementException(format("Module with type '%s' was not found", type));
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T , U extends Annotation> T getHostModule(Class<U> type) {
        final Class<T> u = (Class<T>) moduleTypes
                .stream()
                .filter(t -> getExportedAnnotations(t).contains(type))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                                format("Annotation with type '%s' is not exported by any module", type))
                );
        return getModule(u);
    }


    private Set<Class<?>> getExportedAnnotations(Class<?> o) {
        final Exports exports = o.getAnnotation(Exports.class);
        if(exports != null) {
            return new HashSet<>(Arrays.asList(exports.value()));
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    private <T> T createAndInject(Class<T> clazz) {
        final Object m = getModule(clazz);
        return (T) m;
    }

    private void injectModule(Object m, Class<?> clazz) {
        Set<InjectionSite<?, ?>> injectionSites =
                collectInjectionSites(m, clazz);
        injectionSites.forEach(site -> site.inject(m));
    }

    private Set<InjectionSite<?, ?>> collectInjectionSites(Object m, Class<?> clazz) {
        return injectionSiteReader.read(clazz, m, this);
    }

    private <T> Optional<T> resolveAndInjectModule(Class<T> type) {
        final Class<Module> mtype = (Class<Module>) type;
        final Module module = getModule(mtype);
        injectModule(module, type);
        return (Optional<T>) Optional.of(module);
    }
}
