package io.sunshower.barometer.jaxrs;

import java.lang.annotation.*;

/**
 * Created by haswell on 10/25/16.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Server {
    String host() default "127.0.0.1";
}
