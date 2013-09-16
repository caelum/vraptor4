package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AcceptsConstraint(WithAnnotationAcceptor.class)
@Inherited
public @interface AcceptsWithAnnotations {

	 public Class<? extends Annotation>[] value();
}
