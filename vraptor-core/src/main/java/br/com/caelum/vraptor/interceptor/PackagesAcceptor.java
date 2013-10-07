package br.com.caelum.vraptor.interceptor;

import java.util.Arrays;
import java.util.List;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

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
		this.allowedPackages = Arrays.asList(annotation.value());
	}

}
