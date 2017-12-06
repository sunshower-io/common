package io.sunshower.barometer.rs;

import io.sunshower.barometer.rpc.Remote;
import io.sunshower.barometer.rs.module.JAXRSListeners;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import test.TestService;
import test.TestServiceConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by haswell on 10/25/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@ContextConfiguration(
    classes = {
            JAXRSModuleTest.class,
            TestServiceConfiguration.class
    }
)
@TestExecutionListeners(
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
        value = JAXRSListeners.class
)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class JAXRSModuleTest {


    @LocalServerPort
    private int port;


    @Remote
    private TestService testService;

    @Inject
    private TestRestTemplate testRestTemplate;

    @Test
    public void ensureTestRestTemplateIsInjected() {
        assertThat(testRestTemplate, is(not(nullValue())));
    }

    @Test
    public void ensurePortIsValid() {
        assertThat(port, is(not(0)));
    }
    @Test
    public void ensureRemoteProxyIsInvokableWithInjection() {
        assertThat(testService.sayHelloWithInjection("frap"), is("coolbeans beanfrap"));
    }

    @Test
    public void ensureRemoteProxyIsInvokable() {
        assertThat(testService.sayHello("frap"), is("Hello frap"));
    }

    @Test
    public void ensureRemoteProxyIsInvokableWithContentNegotiation() {
        assertThat(testService.sayHelloXML("frap"), is("Hello frap"));
    }


}
