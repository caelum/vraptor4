package br.com.caelum.vraptor.others;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

/**
 * Produces an instance of {@link org.slf4j.LoggerFactory}.
 * 
 * @author Rodrigo Turini
 * @since 4.0.0
 */
public class LoggerFactory {
	
	@Produces
	public Logger getLogger(InjectionPoint ip){
		Class<?> clazz = ip.getMember().getDeclaringClass();
		return org.slf4j.LoggerFactory.getLogger(clazz);
	}
}
