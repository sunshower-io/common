package io.sunshower.barometer.spring;

import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;
import org.springframework.test.context.ContextConfigurationAttributes;

import java.util.List;
import java.util.Set;

/**
 * Created by haswell on 3/24/16.
 */
public class SpringContextCustomizerFactory implements ContextCustomizerFactory {


    private final Set<Object> modules;
    private final Set<Class<?>> moduleTypes;

    public SpringContextCustomizerFactory(Set<Class<?>> moduleTypes, Set<Object> modules) {
        this.modules = modules;
        this.moduleTypes = moduleTypes;

    }

    @Override
    public ContextCustomizer createContextCustomizer(
            Class<?> testClass,
            List<ContextConfigurationAttributes> configAttributes
    ) {
        return new SpringModuleRegistry(testClass, configAttributes, modules, moduleTypes);
    }
}
