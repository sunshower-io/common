package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Created by haswell on 3/23/16.
 *
 * Marker interface for classes that require Barometer configuration injection.
 *
 * <pre>
 *     &#64;Decorated
 *     &#64;MyConfigurtion("value") // must be exported by a module that is in the span of MyDecoratedClass
 *     public class MyDecoratedClass {
 *     }
 * </pre>
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Decorated {
}
