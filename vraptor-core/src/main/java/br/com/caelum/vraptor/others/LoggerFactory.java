package br.com.caelum.vraptor.others;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;

public class LoggerFactory {
	
	@Produces 
	public Logger getLogger(InjectionPoint ip){
		Class<?> clazz = ip.getMember().getDeclaringClass();
		return org.slf4j.LoggerFactory.getLogger(clazz);
	}

}
