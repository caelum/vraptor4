package br.com.caelum.vraptor4.others;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Extractor {
	
	private Pattern regex = Pattern.compile(".*\\/(.*)\\/(.*)");

	public String extractClassName(String requestURI) {		
		Matcher matcher = matches(requestURI);
		return matcher.group(1);
	}

	public String extractMethodName(String requestURI) {
		Matcher matcher = matches(requestURI);
		return matcher.group(2);
	}

	private Matcher matches(String requestURI) {
		Matcher matcher = regex.matcher(requestURI);
		if(!matcher.matches()){
			throw new IllegalStateException("Should match any uri");
		}
		return matcher;
	}
}
