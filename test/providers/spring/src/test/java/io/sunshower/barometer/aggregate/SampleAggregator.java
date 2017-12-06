package io.sunshower.barometer.aggregate;


import io.sunshower.barometer.Aggregate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Aggregate(SampleAggregatorModule.class)
public @interface SampleAggregator {


}
