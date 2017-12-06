package io.sunshower.barometer.spring;

import io.sunshower.barometer.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.inject.Inject;

/**
 * Created by haswell on 3/24/16.
 */
@Module
@Exports(Enable.class)
@Listeners({AnnotationInjectorListener.class, ListenerDispatcherListener.class})
@Dependencies(SpringBarometerModule.DecoratorInjectorPostProcessor.class)
public class SpringBarometerModule {

    public SpringBarometerModule() {

    }


    @Configuration
    static class DecoratorInjectorPostProcessor implements BeanPostProcessor {


        @Inject
        private Registry registry;


        @Bean
        public DecoratorInjectorPostProcessor decoratorInjectorPostProcessor() {
            return new DecoratorInjectorPostProcessor();
        }


        @EventListener
        public void postProcessBeanFactory(ContextRefreshedEvent event) throws BeansException {
            ConfigurableListableBeanFactory beanFactory =
                    (ConfigurableListableBeanFactory) event.getApplicationContext()
                            .getAutowireCapableBeanFactory();

            for (String s : beanFactory.getBeanDefinitionNames()) {
                final Object o = beanFactory.getBean(s);
                final Class<?> type = o.getClass();
                try {
                    AnnotationInjectorListener.inject(registry, type, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            for (String s : beanFactory.getSingletonNames()) {
                final Object o = beanFactory.getSingleton(s);
                final Class<?> type = o.getClass();
                try {
                    AnnotationInjectorListener.inject(registry, type, o);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            try {
                AnnotationInjectorListener.inject(registry, bean.getClass(), bean);
                return bean;
            } catch (IllegalAccessException e) {
                throw new BeanInitializationException("Failed to inject bean", e);
            }
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    }



}
