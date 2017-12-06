package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Declare all of the dependencies of a module.  Dependencies are transitively resolved
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {
    Class<?>[] value() default {};
}
