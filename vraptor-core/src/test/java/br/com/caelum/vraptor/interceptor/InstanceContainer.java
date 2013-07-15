package br.com.caelum.vraptor.interceptor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;

/**
 * A simple container implementation used for tests
 * 
 * @author guilherme silveira
 */
@SuppressWarnings("unchecked")
public class InstanceContainer implements Container {
	
	public final List<Object> instances;
	
	public InstanceContainer(Object  ...objects) {
		instances = new LinkedList(Arrays.asList(objects));
	}

	public <T> boolean canProvide(Class<T> type) {
		for(Object o : instances) {
			if(type.isAssignableFrom(o.getClass())) {
				return true;
			}
		}
		return false;
	}

	public <T> T instanceFor(Class<T> type) {
		T choosen = null;
		for(Object o : instances) {
			if(type.isAssignableFrom(o.getClass())) {
				choosen = (T) o;
			}
		}
		if(choosen!=null){
			return choosen;
		}	
		throw new VRaptorException("Type "+type+" was not found");
	}

	public boolean isEmpty() {
		return instances.isEmpty();
	}

}
