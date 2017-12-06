package io.sunshower.barometer.jaxrs;

import org.springframework.context.ApplicationContext;

import javax.ws.rs.client.*;

/**
 * Created by haswell on 11/1/16.
 */
public interface ClientDecorator {
    void decorate(RestContext context);
}
