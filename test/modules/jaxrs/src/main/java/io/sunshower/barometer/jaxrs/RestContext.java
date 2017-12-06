package io.sunshower.barometer.jaxrs;

import org.springframework.context.ApplicationContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by haswell on 11/2/16.
 */
public class RestContext {

    private final Client client;
    private final WebTarget target;
    private final Field currentField;
    private final Method currentMethod;
    private final Class<?> currentClass;
    private final ApplicationContext context;


    public RestContext(
            ApplicationContext context,
            WebTarget target,
            Client client,
            Method currentMethod,
            Class<?> currentClass,
            Field currentField
    ) {
        this.target = target;
        this.client = client;
        this.context = context;
        this.currentField = currentField;
        this.currentClass = currentClass;
        this.currentMethod = currentMethod;
    }


    public Field getCurrentField() {
        return currentField;
    }

    public Method getCurrentMethod() {
        return currentMethod;
    }

    public Class<?> getCurrentClass() {
        return currentClass;
    }

    public Client getClient() {
        return client;
    }

    public WebTarget getTarget() {
        return target;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
