package io.sunshower.barometer.spring;

import io.sunshower.barometer.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by haswell on 3/25/16.
 */

@Listener
@Decorated
@RunWith(BarometerRunner.class)
@SingleAggregationSampleTest.Aggregation
public class SingleAggregationSampleTest {

    @Decorator
    private Aggregation aggregation;

    @Decorator
    private ExportedInterface exportedInterface;

    @Inject
    private ExampleClassAggregation exampleClassAggregation;

    @Inject
    private String myName;


    @Inject
    private MyListener listener;

    @Inject
    private String relationshipName;

    private static volatile int calledWithBean;

    private static volatile int calledWithTypedParameter;

    private static volatile int calledWithApplicationContext;

    @Listener.BeforeClass
    public void ensureNamedParameterIsInjected(@Named("myName") String hello) {
        if(hello.equals("Joe")) {
            calledWithBean++;
        }
    }

    @Listener.BeforeClass
    public void ensureApplicationContextIsInjectedIntoLifecycleListener(ApplicationContext ctx) {
        if(ctx != null) {
            calledWithApplicationContext++;
        }
    }

    @Listener.BeforeClass
    public void ensureSpringBeanWithTypeIsInjectedIntoLifecycleListener(ExampleClassAggregation ag) {
        if(ag != null) {
            calledWithTypedParameter++;
        }
    }

    @Test
    public void ensureBeforeClassIsCalledWithApplicationContext() {
        assertThat(calledWithApplicationContext, is(1));
    }

    @Test
    public void ensureBeforeClassIsCalledWithCorrectValue() {
        assertThat(calledWithBean, is(1));
    }

    @Test
    public void ensureMyListenerIsToggled() {
        assertThat(listener.toggled, is(true));
    }

    @Test
    public void ensureExportedInterfaceIsInjected() {
        assertThat(exportedInterface, is(not(nullValue())));
    }

    @Test
    public void ensureAggregationIsInjectedIntoTestClass() {
        assertThat(aggregation, is(not(nullValue())));
    }


    @Test
    public void ensureExampleClassAggregationIsInjected() {
        assertThat(exampleClassAggregation, is(not(nullValue())));
    }


    @Test
    public void ensureExampleClassAggregationAggregationIsInjected() {
        assertThat(
                exampleClassAggregation.getAggregation(),
                is(not(nullValue()))
        );
    }

    @Test
    public void ensureMyNameIsInjected() {
        assertThat(myName, is("Joe"));
    }


    @Test
    public void ensureRelationshipNameIsInjected() {
        assertThat(relationshipName, is("Lisa Joe"));
    }

    @Test
    public void ensureExampleClassExportedInterfaceIsInjected() {
        assertThat(exampleClassAggregation.getExportedInterface(), is(not(nullValue())));
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Aggregate(ExampleClassAggregation.class)
    @interface Aggregation {

    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface ExportedInterface {

    }

    @Listener
    public static class MyListener {
        boolean toggled;

        @Listener.BeforeClass
        public void toggle() {
            toggled = true;
        }

    }


    @Module
    @Decorated
    @Configuration
    @SingleAggregationSampleTest.ExportedInterface
    @Exports(SingleAggregationSampleTest.ExportedInterface.class)
    public static class ExampleClassAggregation {




        @Decorator
        private Aggregation aggregation;

        @Decorator
        private ExportedInterface exportedInterface;

        @Bean
        public MyListener listener() {
            return new MyListener();
        }

        public Aggregation getAggregation() {
            return aggregation;
        }

        public void setAggregation(Aggregation aggregation) {
            this.aggregation = aggregation;
        }

        public ExportedInterface getExportedInterface() {
            return exportedInterface;
        }

        public void setExportedInterface(ExportedInterface exportedInterface) {
            this.exportedInterface = exportedInterface;
        }

        @Bean
        public String myName() {
            return "Joe";
        }

        @Bean
        public String relationshipName(@Named("myName") String joe) {
            return "Lisa " + joe;
        }
    }
}
