package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.util.List;

import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

@Dependent
public class PackagesAcceptor implements AcceptsValidator<AcceptsForPackages> {

	private List<String> allowedPackages;

	@Override
	public boolean validate(ControllerMethod method, ControllerInstance instance) {
		String controllerPackageName = instance.getBeanClass().getPackageName();
		for (String packageName : allowedPackages) {
			if(controllerPackageName.contains(packageName)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize(AcceptsForPackages annotation) {
		this.allowedPackages = asList(annotation.value());
	}
}
