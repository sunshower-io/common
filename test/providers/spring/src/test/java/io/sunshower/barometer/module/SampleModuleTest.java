package io.sunshower.barometer.module;

import io.sunshower.barometer.Enable;
import io.sunshower.barometer.Listener;
import io.sunshower.barometer.spring.BarometerRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by haswell on 3/28/16.
 */

@Listener
@Enable(SampleModule.class)
@RunWith(BarometerRunner.class)
@SampleAnnotation("Hi =)")
public class SampleModuleTest {


    @Inject
    private String sampleAnnotationValue;

    @Test
    public void ensureSampleAnnotationValueIsCorrect() {
        assertThat(sampleAnnotationValue, is("Hi =)"));
    }

}
