package io.sunshower.barometer.rpc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by haswell on 10/25/16.
 */
@Target({
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
})
@Retention(RetentionPolicy.RUNTIME)
public @interface Remote {

}
