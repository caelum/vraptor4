package br.com.caelum.vraptor4;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class StupidRouter {
	
	@Inject private Extractor extractor;

	public Method respondTo(Class<?> controller, String requestURI) {
		
		String controllerName = extractor.extractClassName(requestURI);
		String methodName = extractor.extractMethodName(requestURI);
		System.out.println("Controller name => "+controllerName);
		System.out.println("Method name => "+methodName);
		if (controllerName.equalsIgnoreCase(extractControllerFromName(controller.getSimpleName()))){
			Method[] methods = controller.getDeclaredMethods();
			for (Method method : methods) {
				System.out.println("Metodo encontrado => "+method.getName());
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			}
		}
		return null;
	}
	
	private String extractControllerFromName(String baseName) {
		System.out.println("O que chega no basename antes: "+ baseName);
		baseName = lowerFirstCharacter(baseName).split("\\$")[0];
		System.out.println("BaseName => "+baseName);
		if (baseName.endsWith("Controller")) {
			System.out.println("Extraindo controller name => "+baseName.substring(0, baseName.lastIndexOf("Controller")));
			return baseName.substring(0, baseName.lastIndexOf("Controller"));
		}
		return baseName;
	}
	
    private String lowerFirstCharacter(String baseName) {
        return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
    }

}
