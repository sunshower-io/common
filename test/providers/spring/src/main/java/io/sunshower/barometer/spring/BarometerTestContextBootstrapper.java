package io.sunshower.barometer.spring;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Aggregate;
import io.sunshower.barometer.Enable;
import io.sunshower.barometer.Listeners;
import io.sunshower.barometer.Module;
import io.sunshower.barometer.core.AnnotationModuleReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.test.context.*;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.springframework.test.context.support.DelegatingSmartContextLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.beans.BeanUtils.instantiateClass;

/**
 * Created by haswell on 3/23/16.
 */
public class BarometerTestContextBootstrapper extends DefaultTestContextBootstrapper {

    private static final String DEFAULT_BOOTSTRAP_CONTEXT_CLASS_NAME = "org.springframework.test.context.support.DefaultBootstrapContext";

    private static final String DEFAULT_CACHE_AWARE_CONTEXT_LOADER_DELEGATE_CLASS_NAME =
            BarometerCacheAwareContextLoaderDelegate.class.getName();
            //"org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate";

    private static final Log logger = LogFactory.getLog(BarometerTestContextBootstrapper.class);

    private final Class<?> testClass;
    private final Set<Object> modules;
    private final Set<Class<?>> moduleTypes;

    public BarometerTestContextBootstrapper(Class<?> testClass) {
        this.testClass = testClass;
        setBootstrapContext(createBootstrapContext(testClass));
        this.moduleTypes = new LinkedHashSet<>();
        this.moduleTypes.add(SpringBarometerModule.class);
        this.moduleTypes.addAll(new AnnotationModuleReader().read(testClass));
        this.modules = moduleTypes.stream().map(BeanUtils::instantiate).collect(Collectors.toSet());

    }



    @Override
    public TestContext buildTestContext() {
        return new BarometerTestContext(
                getBootstrapContext().getTestClass(),
                buildMergedContextConfiguration(),
                getCacheAwareContextLoaderDelegate()
        );
    }

    @Override
    protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
        return DelegatingSmartContextLoader.class;
    }

    @Override
    protected List<ContextCustomizerFactory> getContextCustomizerFactories() {
        final List<ContextCustomizerFactory> results =
                super.getContextCustomizerFactories();
        results.add(new SpringContextCustomizerFactory(moduleTypes, modules));

        addCustomizersInSpan(testClass, results);
        return results;
    }


    @Override
    protected Set<Class<? extends TestExecutionListener>> getDefaultTestExecutionListenerClasses() {
        final Set<Class<? extends TestExecutionListener>> listeners = new HashSet<>();
        listeners.addAll(super.getDefaultTestExecutionListenerClasses());
        return moduleTypes.stream()
                .flatMap(m -> getListeners(m))
                .flatMap(t -> cast(t))
                .collect(Collectors.toCollection(() -> listeners));
    }

    private Stream<Class<?>> getListeners(Class<?> m) {
        if(m.isAnnotationPresent(Listeners.class)) {
            return Arrays.stream(m.getAnnotation(Listeners.class).value());
        }
        return Stream.empty();
    }


    @SuppressWarnings("unchecked")
    final Stream<Class<TestExecutionListener>> cast(Class<?> l) {
        if(TestExecutionListener.class.isAssignableFrom(l)) {
            Class<TestExecutionListener> i =
                    (Class<TestExecutionListener>) l.asSubclass(TestExecutionListener.class);
            return Stream.of(i);
        }
        return Stream.empty();
    }


    @SuppressWarnings("unchecked")
    static BootstrapContext createBootstrapContext(Class<?> testClass) {
        CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate = createCacheAwareContextLoaderDelegate();

        Class<? extends BootstrapContext> clazz = null;
        try {
            clazz = (Class<? extends BootstrapContext>) ClassUtils.forName(DEFAULT_BOOTSTRAP_CONTEXT_CLASS_NAME,
                    BarometerTestContextBootstrapper.class.getClassLoader());

            Constructor<? extends BootstrapContext> constructor = clazz.getConstructor(Class.class,
                    CacheAwareContextLoaderDelegate.class);

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Instantiating BootstrapContext using constructor [%s]", constructor));
            }
            return instantiateClass(constructor, testClass, cacheAwareContextLoaderDelegate);
        }
        catch (Throwable t) {
            throw new IllegalStateException("Could not load BootstrapContext [" + clazz + "]", t);
        }
    }

    @SuppressWarnings("unchecked")
    private static CacheAwareContextLoaderDelegate createCacheAwareContextLoaderDelegate() {
        Class<? extends CacheAwareContextLoaderDelegate> clazz = null;
        try {
            clazz = (Class<? extends CacheAwareContextLoaderDelegate>) ClassUtils.forName(
                    DEFAULT_CACHE_AWARE_CONTEXT_LOADER_DELEGATE_CLASS_NAME, BarometerTestContextBootstrapper.class.getClassLoader());

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Instantiating CacheAwareContextLoaderDelegate from class [%s]",
                        clazz.getName()));
            }
            CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate =
                    instantiateClass(clazz, CacheAwareContextLoaderDelegate.class);
            return cacheAwareContextLoaderDelegate;
        }
        catch (Throwable t) {
            throw new IllegalStateException("Could not load CacheAwareContextLoaderDelegate [" + clazz + "]", t);
        }
    }

    protected ContextLoader resolveContextLoader(Class<?> testClass,
                                                 List<ContextConfigurationAttributes> configAttributesList) {

//        Assert.notNull(testClass, "Class must not be null");
//        Assert.notNull(configAttributesList, "ContextConfigurationAttributes list must not be null");
//
//        Class<? extends ContextLoader> contextLoaderClass = resolveExplicitContextLoaderClass(configAttributesList);
//        if (contextLoaderClass == null) {
//            contextLoaderClass = getDefaultContextLoaderClass(testClass);
//            if (contextLoaderClass == null) {
//                throw new IllegalStateException("getDefaultContextLoaderClass() must not return null");
//            }
//        }
//        if (logger.isTraceEnabled()) {
//            logger.trace(String.format("Using ContextLoader class [%s] for test class [%s]",
//                    contextLoaderClass.getName(), testClass.getName()));
//        }
        return BeanUtils.instantiateClass(BarometerContextLoader.class, ContextLoader.class);
    }


    private void addCustomizersInSpan(Class<?> testClass, List<ContextCustomizerFactory> results) {
        if(testClass.isAnnotationPresent(Enable.class)) {
            final Enable enable = testClass.getAnnotation(Enable.class);
            for(Class<?> v : enable.value()) {
                if(v.isAnnotationPresent(Module.class)) {
                    final Module module = v.getAnnotation(Module.class);
                    Class<?>[] vs = module.value();
                    for(Class<?> candidate : vs) {
                        if(ContextCustomizerFactory.class.isAssignableFrom(candidate)) {
                            results.add((ContextCustomizerFactory) Reflect.instantiate(candidate));
                        }
                    }
                }
            }
        }

        Annotation[] annotations = testClass.getAnnotations();
        for(Annotation a : annotations) {
            if(a.annotationType().isAnnotationPresent(Aggregate.class)) {
                final Aggregate ag = a.annotationType().getAnnotation(Aggregate.class);
                for(Class<?> c : ag.value()) {
                    if(c.isAnnotationPresent(Module.class)) {
                        Class<?>[] ms = c.getAnnotation(Module.class).value();
                        for(Class<?> m : ms) {
                            if(ContextCustomizerFactory.class.isAssignableFrom(m)) {
                                results.add((ContextCustomizerFactory) Reflect.instantiate(m));
                            }
                        }
                    }
                }
            }
        }
    }
}
