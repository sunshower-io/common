package io.sunshower.barometer;

import java.util.Collection;

/**
 * Created by haswell on 3/23/16.
 */
public interface ModuleReader<U> {

    Collection<Class<?>> read(U input);

}
