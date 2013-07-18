package br.com.caelum.vraptor4x.interceptor;

public class ParameterClass {

	private Class<?> type;

	public ParameterClass(Class<?> type) {
		super();
		this.type = type;
	}

	@Override
	public int hashCode() {
		//i know... but the proxy class were returning diferent hashcodes and messing all the map.
		return 1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;		
		ParameterClass other = (ParameterClass) obj;
		if (type == null) {			
			if (other.type != null)
				return false;			
		} else {			
			if (!type.isAssignableFrom(other.type)){				
				return false;
			}
		}
		return true;
	}
	
	
}
