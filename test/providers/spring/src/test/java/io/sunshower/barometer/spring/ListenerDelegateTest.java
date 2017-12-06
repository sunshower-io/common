package io.sunshower.barometer.spring;

import io.sunshower.barometer.Listener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyByte;

/**
 * Created by haswell on 3/28/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ListenerDelegateTest {

    @Mock
    private TestContext context;


    private ListenerDispatcherListener listener;

    @Mock
    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        listener = new ListenerDispatcherListener();
    }

    @Test
    public void ensureListenerInvocationFailsForNonPublicMethod() throws Exception {
        @Listener
        class ListenerTest {
            @Listener.AfterClass
            private void onInvoked() {

            }
        }
        final ListenerTest instance = new ListenerTest();
        given(context.getTestInstance()).willReturn(instance);
        given((Object) context.getTestClass()).willReturn(ListenerTest.class);
        given(context.getApplicationContext()).willReturn(applicationContext);
        final Map<String, Object> instances = new HashMap<>();
        instances.put("whatever", instance);
        given(applicationContext.getBeansWithAnnotation(Listener.class)).willReturn(instances);
        listener.prepareTestInstance(context);
        listener.beforeTestClass(context);
    }



    @Test(expected = RuntimeException.class)
    public void ensureListenerInvocationFailsForPrivateMethod() throws Exception {

        @Listener
        class ListenerTest {
            @Listener.AfterClass
            private void onInvoked() {

            }

            @Listener.Before
            public void acceptApplicationContext(ApplicationContext ctx) {

            }
        }
        final ListenerTest instance = new ListenerTest();
        given(context.getTestInstance()).willReturn(instance);
        given((Object) context.getTestClass()).willReturn(ListenerTest.class);
        given(context.getApplicationContext()).willReturn(applicationContext);
        final Map<String, Object> instances = new HashMap<>();
        instances.put("whatever", instance);
        given(applicationContext.getBeansWithAnnotation(Listener.class)).willReturn(instances);
        listener.prepareTestInstance(context);
        listener.afterTestClass(context);

    }

    @Test(expected = RuntimeException.class)
    public void ensureListenerInvocationFailsWhenListenerMethodThrowsException() throws Exception {

        @Listener
        class ListenerTest {
            @Listener.AfterClass
            public void onInvoked() {

                throw new IllegalStateException();
            }

            @Listener.Before
            public void acceptApplicationContext(ApplicationContext ctx) {
                throw new IllegalStateException();
            }
        }
        final ListenerTest instance = new ListenerTest();
        given(context.getTestInstance()).willReturn(instance);
        given((Object) context.getTestClass()).willReturn(ListenerTest.class);
        given(context.getApplicationContext()).willReturn(applicationContext);
        final Map<String, Object> instances = new HashMap<>();
        instances.put("whatever", instance);
        given(applicationContext.getBeansWithAnnotation(Listener.class)).willReturn(instances);
        listener.prepareTestInstance(context);
        listener.afterTestClass(context);
    }

}