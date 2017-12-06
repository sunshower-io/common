package io.sunshower.barometer.spring;

import io.sunshower.barometer.Registry;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

/**
 * Created by haswell on 3/25/16.
 */
@RunWith(BarometerRunner.class)
public class SpringModuleRegistryTest {

    @Inject
    private Registry registry;

    @Test
    public void ensureRegistryIsInjected() {
        assertThat(registry, is(not(nullValue())));
    }

}