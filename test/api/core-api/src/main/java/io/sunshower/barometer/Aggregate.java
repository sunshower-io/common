package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Created by haswell on 3/25/16.
 *
 * Annotations annotated with this meta-annotation define Barometer Aggregates, which allow configurations and
 * modules to be aggregated together and declared as a unit.   Aggregates are resolved according to the
 * topological order of their dependencies.  Cycles may be permitted if they are resolvable.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Aggregate {
    Class<?>[] value();
}
