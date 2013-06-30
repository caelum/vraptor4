package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AcceptsConstraint(PackagesAcceptor.class)
public @interface AcceptsForPackages {
	String[] value();
}
