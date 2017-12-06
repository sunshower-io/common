package test;

import io.sunshower.barometer.rs.module.SelfScanningAutoConfiguration;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 10/25/16.
 */
@Configuration
public class TestServiceConfiguration {
    
    @Bean
    public Holder holder() {
        return new Holder();
    }
    
    
    

    @Bean
    public static SelfScanningAutoConfiguration autoConfiguration(ApplicationContext ctx) {
        return new SelfScanningAutoConfiguration(ctx);
    }

    @Bean
    public TestService testService() {
        return new DefaultTestService();
    }
}
