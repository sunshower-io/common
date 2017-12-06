package io.sunshower.barometer.jaxrs;

import java.lang.annotation.*;

/**
 * Created by haswell on 11/1/16.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.FIELD
})
public @interface ClientContext {

    Class<? extends ClientDecorator> provider()
            default ClientDecorator.class;
}
