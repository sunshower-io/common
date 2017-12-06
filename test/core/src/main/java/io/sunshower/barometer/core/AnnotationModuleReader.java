package io.sunshower.barometer.core;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.*;
import io.sunshower.lambda.Option;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by haswell on 3/23/16.
 */
public class AnnotationModuleReader implements ModuleReader<Class<?>> {

    @Override
    @SuppressWarnings("unchecked")
    public Set<Class<?>> read(Class<?> input) {
        Optional<Enable> classes = Reflect.mapOverHierarchy(input,
                i -> Option.of(i.getAnnotation(Enable.class)))
                .findFirst();
        final Set<Class<?>> results = new HashSet<>();
        classes.ifPresent(enable -> results.addAll(Arrays.asList(enable.value())));
        collect(input, results);
        return results;
    }

    private void collect(Class<?> input, Set<Class<?>> results) {
        collectDependencies(input, results);
        collectEnabledModules(input, results);
        aggregate(input, results);

    }

    private void collectDependencies(Class<?> input, Set<Class<?>> results) {
        if (input.isAnnotationPresent(Dependencies.class)) {
            List<Class<?>> dependencies = Arrays.asList(input.getAnnotation(Dependencies.class).value());
            for (Class<?> dependency : dependencies) {
                if (results.add(dependency)) {
                    collect(dependency, results);
                }
            }
        }
    }

    private void collectEnabledModules(Class<?> input, Set<Class<?>> results) {
        if (input.isAnnotationPresent(Enable.class)) {
            List<Class<?>> dependencies = Arrays.asList(input.getAnnotation(Enable.class).value());
            for (Class<?> dependency : dependencies) {
                if (results.add(dependency)) {
                    collect(dependency, results);
                }
            }
        }
    }

    private void aggregate(Class<?> input, Set<Class<?>> results) {
        if(input.isAnnotationPresent(Module.class)) {
            results.add(input);
        }
        Annotation[] annotations = input.getAnnotations();
        for(Annotation annotation : annotations) {
            final Class<?> annotationType = annotation.annotationType();
            if(annotationType.isAnnotationPresent(Aggregate.class)) {
                final Aggregate aggregate = annotationType.getAnnotation(Aggregate.class);
                for(Class<?> type : aggregate.value()) {
                    collect(type, results);
                }
            }
        }
    }
}
