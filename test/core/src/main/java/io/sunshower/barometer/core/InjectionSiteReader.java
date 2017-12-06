package io.sunshower.barometer.core;

import io.sunshower.barometer.Registry;

import java.util.Set;

/**
 * Created by haswell on 3/24/16.
 */
public interface InjectionSiteReader {
    Set<InjectionSite<?, ?>> read(Class<?> type, Object instance, Registry r);
}
