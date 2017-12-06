package io.sunshower.barometer;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by haswell on 3/22/16.
 *
 * Enable a collection of modules.  If this is commonly used, it may make sense to aggregate the participating
 * modules into a aggregated annotation
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable {

    Class<?>[] value() default {};


}
