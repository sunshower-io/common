package io.sunshower.barometer.rs.module;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.context.ApplicationContext;

import javax.ws.rs.Path;


/**
 * Created by haswell on 5/1/17.
 */
public class SelfScanningAutoConfiguration extends ResourceConfig implements ResourceConfigCustomizer {
    final ApplicationContext applicationContext;

    public SelfScanningAutoConfiguration(ApplicationContext context) {
        this.applicationContext = context;
    }

    private void registerAll(ApplicationContext ctx, ResourceConfig cfg) {

        ConfigurableListableBeanFactory factory =
                (ConfigurableListableBeanFactory) ctx.getAutowireCapableBeanFactory();
        for (String name : factory.getBeanDefinitionNames()) {
            Path path = factory.findAnnotationOnBean(name, Path.class);
            if (path != null) {
                cfg.register(factory.getType(name));
            }
        }
    }


    @Override
    public void customize(ResourceConfig config) {

        registerAll(applicationContext, config);
    }
}


