package io.sunshower.barometer.rs;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Enable;
import io.sunshower.barometer.jaxrs.Port;
import io.sunshower.barometer.jaxrs.Server;
import io.sunshower.barometer.rs.module.JAXRS;
import io.sunshower.barometer.spring.BarometerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import test.TestServiceConfiguration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by haswell on 10/25/16.
 */
@Decorated
@Enable(JAXRS.class)
@RunWith(BarometerRunner.class)
@ContextConfiguration(
        classes = TestServiceConfiguration.class
)
@Server
@WebAppConfiguration
@Port(9192)
public class PortTest {


    @Decorator
    private Port port;

    @Test
    public void ensurePortOverrideHasCorrectValue() {
        assertThat(port.value(), is(9192));
    }

    @Test
    public void ensurePortOverrideWorks() {
        assertThat(port, is(not(nullValue())));
    }
}
