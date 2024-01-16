package com.fmarsh.server.annotation.parameter;

import com.fmarsh.server.annotation.details.ParameterAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@ParameterAnnotation
public @interface RequestBody {}
