package com.ws.java.junit.jupiter.params.provider;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.*;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(YamlFileArgumentsProvider.class)
@SuppressWarnings("exports")
public @interface YamlFileSource {
    String resource() default "";
}
