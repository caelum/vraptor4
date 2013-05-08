package br.com.caelum.vraptor4.others;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.log4j.Logger;

public class LoggerFactory {
	
	@Produces Logger getLogger(InjectionPoint ip){
		Class<?> clazz = ip.getMember().getDeclaringClass();
		return Logger.getLogger(clazz.getName());
	}

}
