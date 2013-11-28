package br.com.caelum.cdi.component;

import static org.mockito.Mockito.mock;

import javax.enterprise.inject.Produces;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class MockValidatorProducer {

	@Produces Validator getValidator(){
		return mock(Validator.class);
	}

	@Produces ValidatorFactory getValidatorFactory(){
		return mock(ValidatorFactory.class);
	}
}