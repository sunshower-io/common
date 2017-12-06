package io.sunshower.barometer.aggregate;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Listener;
import io.sunshower.barometer.spring.BarometerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.inject.Named;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by haswell on 3/28/16.
 */

@Decorated
@SampleAggregator
@Listener
@RunWith(BarometerRunner.class)
@SampleAggregatedAnnotation("Frappus")
public class SampleAggregatorTest {

    @Inject
    private String sayHello;
    
    @Decorator
    private SampleAggregatedAnnotation annotation;

    @Test
    public void ensureSayHelloIsInjected() {
        assertThat(sayHello, is("Hello"));
    }

    
    
    @Test
    public void ensureAggregatedAnnotationIsInjected() {
        assertThat(annotation, is(not(nullValue())));
    }
}
