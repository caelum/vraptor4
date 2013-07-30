package br.com.caelum.cdi.component;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;

import org.junit.Ignore;

@Ignore
public class TestValidatorFactory implements ValidatorFactory{

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConstraintValidatorFactory getConstraintValidatorFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MessageInterpolator getMessageInterpolator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterNameProvider getParameterNameProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TraversableResolver getTraversableResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Validator getValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValidatorContext usingContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
