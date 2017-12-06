package io.sunshower.barometer.core;


import io.sunshower.barometer.*;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by haswell on 3/23/16.
 */
public class DefaultModuleRegistryTest {


    @Test
    public void ensureRegistryLocatesCorrectModuleForAnnotationForSingleModule() throws Exception {
        Registry r = new DefaultModuleRegistry(A.class);
        Object hostModule = r.getHostModule(Uninherited.class);
        assertThat(hostModule.getClass(),
                CoreMatchers.<Class<?>>is(A.class));
    }


    @Test
    public void ensureGetModulesReturnsModules() {
        Registry r = new DefaultModuleRegistry(A.class);
        assertThat(r.getModules().size(), is(1));
        assertThat(r.getModules().contains(A.class), is(true));
    }

    @Test
    public void ensureRetrivingModuleInstanceByTypeWorks() {
        Registry r = new DefaultModuleRegistry(A.class);
        final A m = r.getModule(A.class);
        assertThat(m, is(not(nullValue())));
    }


    @Test(expected = NoSuchElementException.class)
    public void ensureRetrievingNonExistantModuleThrowsCorrectException() {
        class B {}
        Registry r = new DefaultModuleRegistry(A.class);
        final B m = r.getModule(B.class);
    }


    @Test
    public void ensureModuleReaderIsReturnedCorrectly() {
        ModuleReader<Class<?>> reader;
        Registry r = new DefaultModuleRegistry(A.class, (reader = new AnnotationModuleReader()));
        assertThat(r.getModuleReader(), is(reader));
    }

    @Test
    public void ensureInjectingAnnotationInjectsAnnotationOnModule() {
        DefaultModuleRegistry r = new DefaultModuleRegistry(A.class);
        A result = r.inject(A.class);
        assertThat(result, is(not(nullValue())));
        assertThat(result.getEnable(), is(not(nullValue())));
    }




    @Decorated
    public static class AInitiator {
        @Decorator
        private Enable enable;

        public Enable getEnable() {
            return enable;
        }
    }

    @Decorated
    public static class BInitiator {
        @Decorator
        private Enable enable;

        public Enable getEnable() {
            return enable;
        }
    }


    @Decorated
    public static class C {
        @Decorator
        private Enable enable;


        public Enable getEnable() {
            return enable;
        }


    }

    @Decorated
    @Enable({B.class, C.class})
    public static class B {
        @Decorator
        private Enable enable;

        public Enable getEnable() {
            return enable;
        }

    }


    @Decorated
    @Enable(A.class)
    @Module
    @Exports(Uninherited.class)
    public static class A {

        @Decorator
        private Enable enable;

        public A() {

        }

        public Enable getEnable() {
            return enable;
        }

    }



    @Uninherited("test")
    interface OtherInterface {}


    @Uninherited
    interface UninheritedIface {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Uninherited {
        String value() default "";
    }
}