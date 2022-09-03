package test.call_graph.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author adrninistrator
 * @date 2022/8/26
 * @description:
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface TestAnnotationOuter {
    String value();

    TestAnnotationInner[] annotations();
}
