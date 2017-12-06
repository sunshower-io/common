package io.sunshower.barometer.module;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Exports;
import io.sunshower.barometer.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by haswell on 3/28/16.
 */

@Module
@Decorated
@Configuration
@Exports(SampleAnnotation.class)
public class SampleModule {

    @Decorator
    private SampleAnnotation sampleAnnotation;


    @Bean
    public String sampleAnnotationValue() {
        return sampleAnnotation.value();
    }

}
