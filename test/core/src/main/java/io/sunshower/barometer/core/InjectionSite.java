package io.sunshower.barometer.core;

import io.sunshower.barometer.Registry;

import javax.annotation.Nonnull;
import java.lang.reflect.AccessibleObject;

/**
 * Created by haswell on 3/23/16.
 */
public abstract class InjectionSite<T, U extends AccessibleObject> {

    protected final String name;
    protected final Class<T> type;
    protected final Registry registry;


    protected InjectionSite(
            @Nonnull String name,
            @Nonnull Class<T> type,
            @Nonnull Registry registry
    ) {
        this.name = name;
        this.type = type;
        this.registry = registry;
    }

    public abstract <U> U inject(U instance);
}
