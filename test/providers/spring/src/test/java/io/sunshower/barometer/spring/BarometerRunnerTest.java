package io.sunshower.barometer.spring;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Enable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by haswell on 3/22/16.
 */
@Enable
@Decorated
@Configuration
@RunWith(BarometerRunner.class)
@ContextConfiguration(classes = BarometerRunnerTest.class)
public class BarometerRunnerTest {

    @Decorator
    private Enable enable;

    @Inject
    private ApplicationContext context;

    @Test
    public void ensureApplicationContextIsInjectable() {
        assertThat(context, is(not(nullValue())));
    }

    @Test
    public void ensureEmptyEnablementCanBeInjected() {
        assertThat(enable, is(not(nullValue())));
    }
}
