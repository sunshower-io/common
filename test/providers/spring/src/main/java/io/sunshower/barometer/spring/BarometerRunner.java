package io.sunshower.barometer.spring;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by haswell on 3/23/16.
 */
public class BarometerRunner extends SpringJUnit4ClassRunner {

    public BarometerRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
    }


    @Override
    protected TestContextManager createTestContextManager(Class<?> clazz) {
        return new BarometerTestContextManager(new BarometerTestContextBootstrapper(clazz));
    }
}
