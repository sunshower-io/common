package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Created by haswell on 11/1/16.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.FIELD,
        ElementType.TYPE,
        ElementType.ANNOTATION_TYPE
})
public @interface Select {

    String value() default "";

    String name() default "";

    Class<?> type() default void.class;

    Scope scope() default Scope.Default;
}
