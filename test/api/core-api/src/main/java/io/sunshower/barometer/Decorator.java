package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 *
 * Injection annotation for annotation-based Barometer configurations.
 * Target types must be exported by a module within the span of a test configuration
 */
@Inherited
@Documented
@Target({
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.PARAMETER,
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorator {

}
