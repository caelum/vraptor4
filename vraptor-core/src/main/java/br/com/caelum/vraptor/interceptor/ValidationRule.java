package br.com.caelum.vraptor.interceptor;

public interface ValidationRule {

	public void validate(Class<?> originalType);
}
