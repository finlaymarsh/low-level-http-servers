package com.fmarsh.server.annotation.mapping;

import com.fmarsh.server.annotation.details.MappingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@MappingAnnotation
public @interface PostMapping {
    String path();
}
