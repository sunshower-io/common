package io.sunshower.barometer.core;

import io.sunshower.barometer.Enable;
import io.sunshower.barometer.Module;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by haswell on 3/23/16.
 */
public class AnnotationModuleReaderTest {


    @Test
    public void ensureModuleReaderReturnsEmptyListForUnannotatedClass() {
        class A {

        }
        Collection<Class<?>> read =
                new AnnotationModuleReader().read(A.class);
        assertThat(read, is(not(nullValue())));
    }

    @Test
    public void ensureModuleReaderReturnsSingleClassForAnnotatedClass() {
        abstract class A {

        }

        @Enable(A.class)
        class B {

        }

        Set<Class<?>> a = new AnnotationModuleReader().read(B.class);
        assertThat(a.contains(A.class), is(true));
    }

}