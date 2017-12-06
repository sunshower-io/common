package io.sunshower.barometer.rs.module;

import io.sunshower.arcus.reflect.Reflect;
import io.sunshower.barometer.Scope;
import io.sunshower.barometer.Select;
import io.sunshower.barometer.jaxrs.*;
import io.sunshower.barometer.rpc.Remote;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by haswell on 10/25/16.
 */
public class JAXRSListeners extends AbstractTestExecutionListener implements BeanPostProcessor {

    private static final Log logger = LogFactory.getLog(JAXRS.class);

    private final ThreadLocal<RestContext> currentContext;

    public JAXRSListeners() {
        currentContext = new ThreadLocal<>();
    }

//    private static final Map<String, RestContext>
//            targetCache = new ConcurrentHashMap<>();

    static final String DEFAULT_TARGET_KEY = "default-web-target";

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        inject(
                testContext,
                testContext.getTestClass(),
                testContext.getTestMethod(),
                testContext.getTestInstance(),
                testContext.getApplicationContext()
        );

    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        cleanUpContext(testContext, this.currentContext.get());
        this.currentContext.set(null);
    }

    protected void cleanUpContext(TestContext testContext, RestContext restContext) {

    }


    protected void prepareContext(TestContext testContext, RestContext restContext) {

    }


    private void inject(
            TestContext testContext,
            Class<?> type,
            Method testMethod,
            Object instance,
            ApplicationContext context
    ) {
        Reflect.collectOverHierarchy(type,
                t -> Arrays.stream(t.getDeclaredFields())
        ).flatMap(f -> f.isAnnotationPresent(Remote.class) ? Stream.of(f) : Stream.empty())
                .forEach(f -> inject(testContext, f, instance, context, type, testMethod));
    }

    private void inject(
            TestContext testContext,
            Field field,
            Object instance,
            ApplicationContext context,
            Class<?> testClass,
            Method testMethod
    ) {
        final Optional<? extends Class<?>> otype =
                Reflect.collectOverHierarchy(field.getType(),
                        c -> c.isAnnotationPresent(Path.class) ?
                                Stream.of(c) : Stream.empty()).findFirst();


        try {
            RestContext restContext = null;
            if (otype.isPresent()) {
                final Class<?> type = otype.get();
                if (field.isAnnotationPresent(Select.class)) {
                    restContext = injectSelect(instance, field, type, context, testClass, testMethod);
                } else {
                    restContext = injectStandard(instance, field, type, context, testClass, testMethod);
                }
            }
            this.currentContext.set(restContext);
            prepareContext(testContext, restContext);

        } catch (IllegalAccessException ex) {

        }
    }

    private RestContext injectStandard(
            Object instance,
            Field field,
            Class<?> type,
            ApplicationContext context,
            Class<?> testClass, Method testMethod) throws IllegalAccessException {

        int property = context.getEnvironment().getProperty("local.server.port", int.class);

        final RestContext target;
        target = createTarget(
                context,
                property,
                "localhost",
                type,
                testMethod,
                field
        );
        decorate(testClass.getAnnotation(ClientContext.class), target);
        decorate(field.getAnnotation(ClientContext.class), target);
        injectField(instance, field, target, testClass, testMethod);
        return target;
    }

    private void injectField(
            Object instance,
            Field field,
            RestContext target,
            Class<?> testClass,
            Method testMethod
    ) throws IllegalAccessException {
        field.setAccessible(true);
        final Class<?> fieldType = field.getType();

        final WebTarget webTarget = target.getTarget();
        Object o = WebResourceFactory.newResource(fieldType, webTarget);
        field.set(instance, o);
    }

    private RestContext injectSelect(
            Object instance,
            Field field,
            Class<?> type,
            ApplicationContext context,
            Class<?> testClass,
            Method testMethod
    ) throws IllegalAccessException {

        final Select select = field.getAnnotation(Select.class);
        final Scope scope = select.scope();
        final RestContext target;
        if (scope == Scope.Prototype) {
            final Integer port = context.getBean("jax-rs-port", Integer.class);
            final String server = context.getBean("bind-address", String.class);
            target = createTarget(context, port, server, type, testMethod, field);
        } else {
            final Integer port = context.getBean("jax-rs-port", Integer.class);
            final String server = context.getBean("bind-address", String.class);
            target = createTarget(context, port, server, type, testMethod, field);
        }
        decorate(testClass.getAnnotation(ClientContext.class), target);
        decorate(field.getAnnotation(ClientContext.class), target);
        injectField(instance, field, target, testClass, testMethod);
        return target;
    }

    private RestContext createTarget(
            ApplicationContext context,
            Integer port,
            String server,
            Class<?> type,
            Method testMethod,
            Field currentField
    ) {

        final String address = String.format("http://%s:%d/", server, port);
        logger.info("Starting client at " + address);

        final Client client = ClientBuilder.newClient();


        for (Class<?> overrides : annotatedTypes(context)) {
            logger.info("Registering override: " + overrides);
            client.register(overrides);
        }

        final WebTarget target = client.target(address);

        final RestContext ctx = new RestContext(
                context,
                target,
                client,
                testMethod,
                type,
                currentField
        );
        if (type.isAnnotationPresent(ClientContext.class)) {
            decorate(type.getAnnotation(ClientContext.class), ctx);
        }
        return ctx;
    }

    private Set<Class<?>> annotatedTypes(ApplicationContext context) {
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory)
                context.getAutowireCapableBeanFactory();
        return Arrays.stream(beanDefinitionNames).flatMap(t -> {
            final BeanDefinition definition = factory.getBeanDefinition(t);
            try {
                String name = definition.getBeanClassName();

                if(name != null) {
                    final Class<?> type = Class.forName(name);
                    if (type.isAnnotationPresent(ProviderOverride.class)) {
                        return Stream.of(type);
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.error("Failed to locate class {0}", e);
                throw new IllegalStateException(e);
            }
            return Stream.empty();
        }).collect(Collectors.toSet());

    }

    private void decorate(ClientContext annotation, RestContext target) {
        if (annotation != null) {
            Class<? extends ClientDecorator> providerType = annotation.provider();

            try {
                Constructor<? extends ClientDecorator>
                        constructor = providerType.getConstructor();
                constructor.setAccessible(true);
                ClientDecorator decorator = constructor.newInstance();
                decorator.decorate(target);
            } catch (Exception e) {
                logger.error("Failed to decorate client.  Reason: ", e);
            }
        }

    }

}
