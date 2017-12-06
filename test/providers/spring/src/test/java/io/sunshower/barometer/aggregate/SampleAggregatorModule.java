package io.sunshower.barometer.aggregate;

import io.sunshower.barometer.Exports;
import io.sunshower.barometer.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 3/28/16.
 */
@Module
@Configuration
@Exports(SampleAggregatedAnnotation.class)
public class SampleAggregatorModule {

    @Bean
    public String sayHello() {
        return "Hello";
    }

}
