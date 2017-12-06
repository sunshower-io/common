package io.sunshower.barometer.spring;

import io.sunshower.barometer.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by haswell on 3/22/16.
 */
@Listener
@RunWith(BarometerRunner.class)
@ContextConfiguration(classes =
        SeparateBarometerRunnerConfigurationTest.Cfg.class)
@Enable(SeparateBarometerRunnerConfigurationTest.TestModule.class)
public class SeparateBarometerRunnerConfigurationTest {

    @Inject
    private Cfg cfg;

    @Inject
    private TestModule testModule;



    @Listener.AfterClass
    public void afterClass() {
        System.out.println("After Class");
    }

    @Listener.BeforeClass
    public void beforeClass() {
        System.out.println("Before Class");
    }

    @Listener.Before
    public void before() {
        System.out.println("Before");
    }

    @Listener.After
    public void after(@Decorator TestEnum context) {
        System.out.println("After" + context);
    }

    @Test
    public void ensureConfigurationIsInjected() {
        assertThat(cfg, is(not(nullValue())));
    }

    @Test
    public void ensureAnnotationIsInjectedIntoConfiguration() {
        assertThat(cfg.enable, is(not(nullValue())));
    }

    @Test
    public void ensureModuleIsInjectable() {
        assertThat(testModule, is(not(nullValue())));
    }

    @Test
    public void ensureModuleExportsAreInjectedIntoExportedModule() {
        assertThat(testModule.testEnum, is(not(nullValue())));
    }

    @Test
    public void ensureTestDecoratorIsInjected() {


    }


    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestEnum {

    }

    @TestEnum
    @Module
    @Decorated
    @Exports(TestEnum.class)
    public static class TestModule {
        @Decorator
        TestEnum testEnum;
    }

    @Decorated
    @Configuration
    public static class Cfg {
        @Decorator
        public Enable enable;

        @Bean
        public TestDecorator testDecorator() {
            return new TestDecorator();
        }

    }

    public static class TestDecorator {
        @Decorator
        public TestEnum testEnum;

        @Decorator
        public Enable enable;

    }

}
