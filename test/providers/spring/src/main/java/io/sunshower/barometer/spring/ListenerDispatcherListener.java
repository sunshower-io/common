package io.sunshower.barometer.spring;

import javax.inject.Named;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Listener;
import io.sunshower.barometer.Registry;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by haswell on 3/24/16.
 */
public class ListenerDispatcherListener implements TestExecutionListener {

    static final Object lock = new Object();

    private volatile boolean prepared;

    private final Set<ListenerDelegate> delegates;

    public ListenerDispatcherListener() {
        this.prepared = false;
        delegates = new HashSet<>();
    }


    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        if(!prepared) {
            synchronized(lock) {
                Map<String, Object> beansWithAnnotation = testContext.getApplicationContext()
                        .getBeansWithAnnotation(Listener.class);
                beansWithAnnotation.forEach((k, v) ->
                        delegates.add(new ListenerDelegate(v.getClass(), v, testContext)));
                delegates.add(new ListenerDelegate(testContext.getTestClass(), testContext.getTestInstance(), testContext));
                delegates.forEach(ListenerDelegate::beforeClass);
                prepared = true;
            }
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        delegates.forEach(ListenerDelegate::before);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        delegates.forEach(ListenerDelegate::after);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        delegates.forEach(ListenerDelegate::afterClass);
    }


    static final class ListenerDelegate {
        final Object instance;
        final Registry registry;
        final Class<?> listener;
        final TestContext context;
        final ApplicationContext applicationContext;

        ListenerDelegate(
                Class<?> listener,
                Object instance,
                TestContext context) {
            this.listener = listener;
            this.instance = instance;
            this.context = context;
            this.applicationContext = context.getApplicationContext();
            this.registry = applicationContext.getBean(Registry.class);
        }

        void beforeClass() {
            Reflect.collectOverHierarchy(listener, (c) -> Arrays.stream(c.getDeclaredMethods()))
                    .filter(c -> c.isAnnotationPresent(Listener.BeforeClass.class))
                    .forEach(this::invoke);
        }

        void afterClass() {
            Reflect.collectOverHierarchy(listener, (c) -> Arrays.stream(c.getDeclaredMethods()))
                    .filter(c -> c.isAnnotationPresent(Listener.AfterClass.class))
                    .forEach(this::invoke);
        }

        void before() {
            Reflect.collectOverHierarchy(listener, (c) -> Arrays.stream(c.getDeclaredMethods()))
                    .filter(c -> c.isAnnotationPresent(Listener.Before.class))
                    .forEach(this::invoke);
        }

        void after() {
            Reflect.collectOverHierarchy(listener, (c) -> Arrays.stream(c.getDeclaredMethods()))
                    .filter(c -> c.isAnnotationPresent(Listener.After.class))
                    .forEach(this::invoke);
        }

        private void invoke(Method m) {
            final Object[] parameters = resolveParametersFromContext(m.getParameters());
            try {
                m.invoke(instance, parameters);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Listener method must be public");
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Listener method threw exception", e);
            }
        }

        private Object[] resolveParametersFromContext(Parameter[] parameters) {
            return Arrays.stream(parameters)
                    .map(this::resolveParameter)
                    .toArray();
        }

        private Object resolveParameter(Parameter p) {
            if(p.isAnnotationPresent(Named.class)) {
                return applicationContext
                        .getBean(p.getAnnotation(Named.class).value(), p.getType());
            } else if(p.isAnnotationPresent(Decorator.class)){
                return registry.getAnnotation(p.getType().asSubclass(Annotation.class));
            }
            else if(ApplicationContext.class.isAssignableFrom(p.getType())) {
                return applicationContext;
            } else {
                return applicationContext.getBean(p.getType());
            }
        }
    }


}
