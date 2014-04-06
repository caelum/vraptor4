package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.util.List;

import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Dependent
public class PackagesAcceptor implements AcceptsValidator<AcceptsForPackages> {

	private List<String> allowedPackages;

	@Override
	public boolean validate(ControllerMethod method, ControllerInstance instance) {
		String controllerPackageName = instance.getBeanClass().getPackageName();
		
		return FluentIterable.from(allowedPackages)
			.anyMatch(currentOrSubpackage(controllerPackageName));
	}

	private Predicate<String> currentOrSubpackage(final String controllerPackageName) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return controllerPackageName.contains(input);
			}
		};
	}

	@Override
	public void initialize(AcceptsForPackages annotation) {
		this.allowedPackages = asList(annotation.value());
	}
}
