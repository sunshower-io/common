package io.sunshower.barometer;

import java.lang.annotation.*;

/**
 * Types annotated with this annotation are modules and may export any Barometer services or declare modular dependencies
 *
 * @since v 1.0.0
 * <pre>
 *
 * &#64;Enable(MyModule.class)
 * &#64;RunWith(BarometerRunner.class)
 * &#64;MyAnnotation("Hello")
 * public class BarometerTest {
 *
 *
 * }
 *
 * &#64;Module
 * &#64;Exports(MyAnnotation.class)
 * &#64;Decorated
 * public class MyModule {
 *
 * &#64;Decorated
 * MyAnnotation myAnnotationInstance;
 *
 * // Configure MyModule with MyAnnotation values
 * }
 *
 * &#64;RetentionPolicy(RetentionPolicy.RUNTIME)
 * public &#64;interface MyAnnotation {
 * String value();
 *
 * }
 * </pre>
 */

@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

    Class<?>[] value() default {};


}
