package io.sunshower.barometer.core;


import java.util.Optional;
import java.lang.reflect.Field;
import javax.annotation.Privileged;
import io.sunshower.barometer.Registry;
import java.util.NoSuchElementException;


public class FieldInjectionSite<T> extends InjectionSite<T, Field> {

    private final Field field;

    @Privileged(RuntimePermission.class)
    public FieldInjectionSite(
            String name,
            Class<T> type,
            Registry registry,
            Field field
    ) {
        super(name, type, registry);
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    public <U> U inject(U instance) {
        try {
            field.set(instance, resolve());
        } catch(IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        return instance;
    }

    private Object resolve() {
        Optional<?> o = registry.find((Class<?>) field.getType(), name);
        if(o.isPresent()) {
            return o.get();
        }
        o = registry.find(name);
        if(o.isPresent()) {
            return o.get();
        }
        o = registry.find(field.getType());
        if(o.isPresent()){
            return o.get();
        }
        throw new NoSuchElementException("Failed to resolve injection site");
    }

}
