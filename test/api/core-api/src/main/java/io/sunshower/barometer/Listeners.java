package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Created by haswell on 3/24/16.
 *
 * Declare a set of listeners (provider dependent test lifecycle listeners to the Barometer implementation)
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listeners {
    Class<?>[] value() default {};
}
