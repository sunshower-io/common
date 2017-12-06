package io.sunshower.barometer.spring;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Registry;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by haswell on 3/24/16.
 */
class AnnotationInjectorListener extends AbstractTestExecutionListener {


    private Registry registry;

    private ApplicationContext context;

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        this.context = testContext.getApplicationContext();
        this.registry = context.getBean(Registry.class);
    }

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        inject(registry, testContext.getTestClass(), testContext.getTestInstance());
    }

    static boolean inject(Registry registry, Class<?> testClass, Object instance) throws IllegalAccessException {
        if(testClass.isAnnotationPresent(Decorated.class)) {
            final Set<Field> toDecorate = Reflect.collectOverHierarchy(
                    testClass,
                    (c) -> Arrays.stream(c.getDeclaredFields())
            ).flatMap(f -> f.isAnnotationPresent(Decorator.class) ?
                    Stream.of(f) : Stream.empty()).collect(Collectors.toSet());
            for(Field f : toDecorate) {
                injectField(registry, instance, f);
            }
            return true;
        }
        return false;
    }

    private static void injectField(Registry registry, Object instance, Field f) throws IllegalAccessException {
        final Optional<?> value =
                registry.find(f.getType());
        if(value.isPresent()) {
            f.setAccessible(true);
            f.set(instance, value.get());
        } else {
            throw new NoSuchElementException("Failed to locate object with type '" +
                    f.getType() + "'.  Is it registered?");
        }
    }

}
