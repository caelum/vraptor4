package br.com.caelum.vraptor4;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StupidRouter {
	
	@Inject private Extractor extractor;

	public Method respondTo(Object controller, String requestURI) {
		
		String controllerName = extractor.extractClassName(requestURI);
		String methodName = extractor.extractMethodName(requestURI);
		
		if (controllerName.equalsIgnoreCase(extractControllerFromName(controllerName.getClass().getName()))){
			Method[] methods = controller.getClass().getMethods();
			for (Method method : methods) {
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			}
		}
		return null;
	}
	
	private String extractControllerFromName(String baseName) {
		baseName = lowerFirstCharacter(baseName);
		if (baseName.endsWith("Controller")) {
			return baseName.substring(0, baseName.lastIndexOf("Controller"));
		}
		return baseName;
	}
	
    private String lowerFirstCharacter(String baseName) {
        return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
    }

}
