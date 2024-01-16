package com.fmarsh.server.annotation.clazz;

import com.fmarsh.server.annotation.details.ClazzAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ClazzAnnotation
public @interface RestController {
}
