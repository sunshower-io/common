package io.sunshower.barometer.spring;

import io.sunshower.barometer.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by haswell on 3/25/16.
 */
@Listener
@Decorated
@RunWith(BarometerRunner.class)
@Enable({ModuleWithDependentModulesTest.InitiatorModule.class})
public class ModuleWithDependentModulesTest {

    private static int beforeClassWithDecoratorCalled;


    @Inject
    private InitiatorModule initiatorModule;

    @Inject
    private DependedUponModule dependedModule;

    @Decorator
    private InitiatorModule.InitiatorAnnotation initiatorAnntation;

    @Listener.BeforeClass
    public void beforeClassWithDecorator(@Decorator InitiatorModule.InitiatorAnnotation annotation) {
        beforeClassWithDecoratorCalled++;
        assertThat(annotation, is(not(nullValue())));
    }

    @Listener.AfterClass
    public void afterClassWithDecorator() {

    }

    @Test
    public void ensureInitiatorAnnotationIsInjected() {
        assertThat(initiatorAnntation, is(not(nullValue())));
    }

    @Test
    public void ensureInitiatorModuleIsInjected() {
        assertThat(initiatorModule, is(not(nullValue())));
    }

    @Test
    public void ensureDependentModuleIsInjected() {
        assertThat(dependedModule, is(not(nullValue())));
    }

    @Test
    public void ensureInitiatorModuleHasInitiatorAnnotation() {
        assertThat(initiatorModule.getInitiatorAnnotation(), is(not(nullValue())));
    }

    @Test
    public void ensureDependedUponModuleHasInitiatorAnnotation() {
        assertThat(dependedModule.getInitiatorAnnotation(), is(not(nullValue())));
    }

    @Test
    public void ensureBeforeClassWithDecoratorCalled() {
        assertThat(beforeClassWithDecoratorCalled, is(1));
    }


    @Module
    @Decorated
    @InitiatorModule.InitiatorAnnotation
    @Dependencies(DependedUponModule.class)
    public static class InitiatorModule {

        @Retention(RetentionPolicy.RUNTIME)
        @interface InitiatorAnnotation{ }

        @Decorator
        private InitiatorAnnotation initiatorAnnotation;

        @Decorator
        private DependedUponModule.DependentAnnotation dependentAnnotation;

        public InitiatorAnnotation getInitiatorAnnotation() {
            return initiatorAnnotation;
        }

        public DependedUponModule.DependentAnnotation getDependentAnnotation() {
            return dependentAnnotation;
        }
    }

    @Module
    @Decorated
    @DependedUponModule.DependentAnnotation
    @Exports(DependedUponModule.DependentAnnotation.class)
    public static class DependedUponModule {
        @Retention(RetentionPolicy.RUNTIME)
        @interface DependentAnnotation { }

        @Decorator
        private DependentAnnotation dependentAnnotation;

        @Decorator
        private InitiatorModule.InitiatorAnnotation initiatorAnnotation;

        public DependentAnnotation getDependentAnnotation() {
            return dependentAnnotation;
        }

        public InitiatorModule.InitiatorAnnotation getInitiatorAnnotation() {
            return initiatorAnnotation;
        }
    }

}
