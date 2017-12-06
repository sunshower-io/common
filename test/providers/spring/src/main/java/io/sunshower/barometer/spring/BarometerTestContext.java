package io.sunshower.barometer.spring;

import io.sunshower.barometer.Enable;
import io.sunshower.barometer.Module;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.DefaultTestContext;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by haswell on 3/24/16.
 */
public class BarometerTestContext extends DefaultTestContext {


    public BarometerTestContext(
            Class<?> testClass,
            MergedContextConfiguration mergedContextConfiguration,
            CacheAwareContextLoaderDelegate cacheAwareContextLoaderDelegate
    ) {
        super(testClass, mergedContextConfiguration, cacheAwareContextLoaderDelegate);

    }

    @Override
    public void updateState(
            Object testInstance, 
            Method testMethod, 
            Throwable testException
    ) {
        super.updateState(testInstance, testMethod, testException);
    }
}
