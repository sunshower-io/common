package io.sunshower.barometer.spring;

import io.sunshower.barometer.*;
import io.sunshower.barometer.core.AnnotationModuleReader;
import io.sunshower.barometer.core.ContextInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

import java.lang.annotation.Annotation;
import java.util.*;

import static io.sunshower.arcus.reflect.Reflect.instantiate;


/**
 * Created by haswell on 3/24/16.
 */
public class SpringModuleRegistry
        implements
        Registry,
        ContextInjector,
        ContextCustomizer {


    final Class<?> testClass;
    final Set<Object> modules;
    final ModuleReader<Class<?>> reader;
    final Set<Class<?>> moduleTypes;
    private ConfigurableApplicationContext context;
    private final Map<Class<?>, ApplicationContext> rootContextCache;

    public SpringModuleRegistry(Class<?> testClass,
                                List<ContextConfigurationAttributes> configAttributes,
                                Set<Object> modules,
                                Set<Class<?>> moduleTypes
    ) {
        this.testClass = testClass;
        this.reader = new AnnotationModuleReader();
        this.modules = modules;
        this.moduleTypes = new HashSet<>();
        this.moduleTypes.addAll(moduleTypes);
        for(Class<?> moduleType : moduleTypes) {
            registerImplicit(moduleType);
        }
        registerAggregations(testClass);
        this.rootContextCache = new HashMap<>();
    }



    @Override
    public ModuleReader getModuleReader() {
        return reader;
    }

    @Override
    public <T> Optional<T> find(String name) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> find(Class<T> type) {
        if(Annotation.class.isAssignableFrom(type)) {
            return (Optional<T>) searchForAnnotations((Class<? extends Annotation>) type.asSubclass(Annotation.class));
        }
        return Optional.empty();
    }

    private <T extends Annotation> Optional<T> searchForAnnotations(Class<T> type) {
        if(testClass.isAnnotationPresent(type)) {
            return Optional.of(testClass.getAnnotation(type));
        }
        T result = searchForAnnotationInModules(type);
        if(result != null) {
            return Optional.of(result);
        }
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(type);
        if(!beansWithAnnotation.isEmpty()) {
            final Object o = beansWithAnnotation.values().iterator().next();
            return Optional.of(o.getClass().getAnnotation(type));
        }
        return Optional.empty();
    }

    private <T extends Annotation> T searchForAnnotationInModules(Class<T> type) {
        for(Class<?> m : moduleTypes) {
            if(m.isAnnotationPresent(type)) {
                return m.getAnnotation(type);
            }
        }
        return null;
    }

    @Override
    public Set<Class<?>> getModules() {
        return null;
    }

    @Override
    public <T> Optional<T> find(Class<T> type, String name) {
        return null;
    }

    @Override
    public <T> T getModule(Class<T> type) {
        return null;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> type) {
        return find(type).get();
    }

    @Override
    public <T, U extends Annotation> T getHostModule(Class<U> type) {
        return null;
    }

    @Override
    public <T> T inject(T instance) {
        try {
            AnnotationInjectorListener.inject(this, instance.getClass(), instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return instance;
    }

    @Override
    public <T> T inject(Class<T> clazz) {
        return inject(instantiate(clazz));
    }

    @Override
    public void customizeContext(
            ConfigurableApplicationContext context,
            MergedContextConfiguration mergedConfig
    ) {
        final ApplicationContext rootContext;
        if(!rootContextCache.containsKey(testClass)) {
            rootContext = createBarometerContext();
        } else {
            rootContext = rootContextCache.get(testClass);
        }
        context.setParent(rootContext);
    }

    private ApplicationContext createBarometerContext() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        this.context = context;
        for(Class<?> moduleType : moduleTypes) {
            if(moduleType.isAnnotationPresent(Configuration.class) &&
                    !moduleType.isAnnotationPresent(Immediate.class)) {
                context.register(moduleType);
            } else {
                context.getBeanFactory().registerSingleton(moduleType.getName(), instantiate(moduleType));
            }
        }


        context.getBeanFactory().registerSingleton("registry", this);
        context.refresh();
        return context;
    }

    private void registerDependencies(Class<?> moduleType) {
        final Dependencies annotation = moduleType.getAnnotation(Dependencies.class);
        for(Class<?> clazz : annotation.value()) {
            this.moduleTypes.add(clazz);
            if(clazz.isAnnotationPresent(Dependencies.class)) {
                registerDependencies(clazz);
            }
        }
    }

    private void registerDependents(Class<?> moduleType) {
        final Enable annotation = moduleType.getAnnotation(Enable.class);
        for(Class<?> clazz : annotation.value()) {
            moduleTypes.add(moduleType);
            if(clazz.isAnnotationPresent(Enable.class)) {
                registerDependents(clazz);
            }
        }
    }

    private void registerImplicit(Class<?> moduleType) {
        if(moduleType.isAnnotationPresent(Enable.class)) {
            registerDependents(moduleType);
        }
        if(moduleType.isAnnotationPresent(Dependencies.class)) {
            registerDependencies(moduleType);
        }
        registerAggregations(moduleType);
    }

    private void registerAggregations(Class<?> moduleType) {
        if(moduleType.isAnnotationPresent(Module.class)) {
            this.moduleTypes.add(moduleType);
        }
        final Annotation[] annotations = moduleType.getAnnotations();
        for(Annotation annotation : annotations) {
            final Class<?> annotationType = annotation.annotationType();
            final Aggregate aggregate =
                    annotationType.getAnnotation(Aggregate.class);
            if(aggregate != null) {
                final Class<?>[] aggregatedTypes = aggregate.value();
                for(Class<?> aggregatedType : aggregatedTypes) {
                    registerImplicit(aggregatedType);
                    if(moduleType.isAnnotationPresent(Module.class)) {
                        this.moduleTypes.add(moduleType);
                    }
                }
            }
        }
    }
}
