package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Created by haswell on 3/24/16.
 *
 * Declare a set of annotations for exportation.  When an annotation is exported, any class within the span
 * of a module can be injected via @Decorator
 */

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Exports {
    Class<? extends Annotation>[] value() default {};
}
