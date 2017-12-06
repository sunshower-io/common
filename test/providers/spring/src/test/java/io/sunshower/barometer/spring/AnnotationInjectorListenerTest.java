package io.sunshower.barometer.spring;

import io.sunshower.barometer.Decorated;
import io.sunshower.barometer.Decorator;
import io.sunshower.barometer.Exports;
import io.sunshower.barometer.Registry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * Created by haswell on 3/28/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotationInjectorListenerTest {



    @Mock
    private Registry registry;


    @SuppressWarnings("unchecked")
    @Test(expected = NoSuchElementException.class)
    public void ensureInjectingClassWithoutValueThrowsExpectedException() throws Exception {

        class B {};
        @Decorated
        class A {
            @Decorator
            private B b;
        }
        given(registry.find((Class<Object>) any())).willReturn(Optional.empty());
        A instance = new A();

        AnnotationInjectorListener.inject(registry, A.class, instance);

    }

}