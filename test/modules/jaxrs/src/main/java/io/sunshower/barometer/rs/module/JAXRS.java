package io.sunshower.barometer.rs.module;

import io.sunshower.barometer.*;
import io.sunshower.barometer.jaxrs.Port;
import io.sunshower.barometer.jaxrs.Server;
import io.sunshower.barometer.rpc.Remote;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyProperties;
import org.springframework.boot.autoconfigure.jersey.ResourceConfigCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.jaxrs.PortMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Named;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by haswell on 10/25/16.
 */

@Module
@Decorated
@Port
@Server
@Immediate
@Configuration
@WebAppConfiguration
@Exports({Remote.class, Port.class})
@Listeners({
        JAXRSListeners.class,
        ServletTestExecutionListener.class
})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@EnableAutoConfiguration
public class JAXRS {

    private static final Log logger = LogFactory.getLog(JAXRS.class);


    @Bean
    public Server server(ApplicationContext context) {
        final Map<String, Object> portHolders =
                context.getBeansWithAnnotation(Server.class);
        for (Map.Entry<String, Object> value : portHolders.entrySet()) {
            Server result = context.findAnnotationOnBean(value.getKey(), Server.class);
            if (result == null) {
                throw new IllegalStateException("Cannot find server annotation");
            }
            return result;
        }
        return JAXRS.class.getAnnotation(Server.class);
    }

    @Bean
    @Singleton
    public Port port(ApplicationContext context) {
        final Map<String, Object> portHolders =
                context.getBeansWithAnnotation(Port.class);
        for (Map.Entry<String, Object> value : portHolders.entrySet()) {
            Port result = context.findAnnotationOnBean(value.getKey(), Port.class);
            if (result == null) {
                throw new IllegalStateException("Cannot find port annotation");
            }
            return result;
        }
        return JAXRS.class.getAnnotation(Port.class);
    }

    @Named("jax-rs-port")
    @Bean(name = "jax-rs-port")
    public Integer portValue(Port port) {

        if (port == null) {
            return PortFinder.freePort();
        }
        if (port.mode() == PortMode.Dynamic) {
            return PortFinder.freePort();
        } else {
            return port.value();
        }
    }

    @Named("bind-address")
    @Bean(name = "bind-address")
    public String bindAddress(Server server) {
        return server.host();
    }

    @Bean
    @Named("rest-location")
    public String location(@Named("bind-address") String bind, @Named("jax-rs-port") Integer port) {

        return String.format("http://%s:%d/", bind, 8080);
    }
//
//    @Bean
//    public SpringBeanProcessor springBeanProcessor(TJWSEmbeddedJaxrsServer server) {
//        SpringBeanProcessor processor = new SpringBeanProcessor(
//                server.getDeployment().getDispatcher(),
//                server.getDeployment().getRegistry(),
//                server.getDeployment().getProviderFactory());
//
//        return processor;
//    }
//
//    @Bean
//    public Client client() {
//        return ClientBuilder.newClient();
//    }
//
//    @Bean
//    public ResteasyWebTarget proxyFactory(
//            @Named("bind-address") String bindAddress,
//            @Named("jax-rs-port") Integer port
//    ) {
//
//
//        final Client client =
//                ClientBuilder.newClient();
//        ResteasyClient rclient = (ResteasyClient)  client;
//        rclient.register(MOXyJsonProvider.class);
//        final String address = String.format("http://%s:%d/", bindAddress, port);
//        logger.info("Starting client at " + address);
//        final ResteasyWebTarget target =
//                (ResteasyWebTarget) client.target(address);
//        return target;
//    }
//
//
//    @Bean
//    @Singleton
//    public TJWSEmbeddedJaxrsServer embeddedJaxrsServer(@Named("jax-rs-port") Integer port,
//                                                       @Named("bind-address") String bindAddress
//                                                       ) {
//        final TJWSEmbeddedJaxrsServer server = new TJWSEmbeddedJaxrsServer();
//        logger.info("Starting server at " + String.format("http://%s:%d/", bindAddress, port));
//        server.setBindAddress(bindAddress);
//        server.setPort(port);
//        server.getDeployment()
//                .getActualProviderClasses()
//                .add(MOXyJsonProvider.class);
//        server.start();
//        return server;
//    }


}
