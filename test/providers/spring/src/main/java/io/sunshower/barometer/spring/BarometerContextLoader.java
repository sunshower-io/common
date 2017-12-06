package io.sunshower.barometer.spring;

import io.sunshower.barometer.Aggregate;
import io.sunshower.barometer.Enable;
import io.sunshower.barometer.Immediate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractGenericContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoaderUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by haswell on 11/1/16.
 */
public class BarometerContextLoader extends AbstractGenericContextLoader {


    private static final Log logger = LogFactory.getLog(AnnotationConfigContextLoader.class);

    @Override
    public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
        if (!configAttributes.hasClasses() && isGenerateDefaultLocations()) {
            configAttributes.setClasses(detectDefaultConfigurationClasses(configAttributes.getDeclaringClass()));
        }
        final List<Class<?>> classes =
                new ArrayList<>(Arrays.asList(configAttributes.getClasses()));
        final Enable enable = configAttributes.getDeclaringClass().getAnnotation(Enable.class);
        if(enable != null) {
            for(Class<?> clazz : enable.value()) {
                if(clazz.isAnnotationPresent(Immediate.class)) {
                    classes.add(clazz);
                }
            }
        }




        final Class<?> type = configAttributes.getDeclaringClass();

        for(Annotation annotation : type.getAnnotations()) {
            if(annotation.annotationType().isAnnotationPresent(Aggregate.class)) {
                final Aggregate aggregate = annotation.annotationType()
                        .getAnnotation(Aggregate.class);
                for(Class<?> immediateCandidate : aggregate.value()) {
                    if(immediateCandidate.isAnnotationPresent(Immediate.class)) {
                        classes.add(immediateCandidate);
                    }
                }
            }
        }


        configAttributes.setClasses(classes.toArray(new Class<?>[0]));
    }

    protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
        Class[] clazzes = AnnotationConfigContextLoaderUtils.detectDefaultConfigurationClasses(declaringClass);
        return clazzes;
    }

    @Override
    protected String[] modifyLocations(Class<?> clazz, String... locations) {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the modifyLocations(Class, String...) method");
    }

    @Override
    protected String[] generateDefaultLocations(Class<?> clazz) {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the generateDefaultLocations(Class) method");
    }

    @Override
    protected String getResourceSuffix() {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the getResourceSuffix() method");
    }

    @Override
    protected void validateMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
        if (mergedConfig.hasLocations()) {
            String msg = String.format(
                    "Test class [%s] has been configured with @ContextConfiguration's 'locations' (or 'value') attribute %s, "
                            + "but %s does not support resource locations.", mergedConfig.getTestClass().getName(),
                    ObjectUtils.nullSafeToString(mergedConfig.getLocations()), getClass().getSimpleName());
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    @Override
    protected void loadBeanDefinitions(GenericApplicationContext context, MergedContextConfiguration mergedConfig) {
        Class<?>[] annotatedClasses = mergedConfig.getClasses();
        if (logger.isDebugEnabled()) {
            logger.debug("Registering annotated classes: " + ObjectUtils.nullSafeToString(annotatedClasses));
        }
        new AnnotatedBeanDefinitionReader(context).register(annotatedClasses);
    }

    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(GenericApplicationContext context) {
        throw new UnsupportedOperationException(
                "AnnotationConfigContextLoader does not support the createBeanDefinitionReader(GenericApplicationContext) method");
    }
}
