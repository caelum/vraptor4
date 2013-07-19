package br.com.caelum.vraptor4.core;

import java.lang.annotation.Annotation;

public class StereotypeInfo {

	private final Class<? extends Annotation> stereotype;
	private final Class<?> stereotypeClass;
	private final Annotation stereotypeQualifier;

	public StereotypeInfo(Class<? extends Annotation> stereotype,
			Class<?> stereotypeClass,
			Annotation stereotypeQualifier) {
		super();
		this.stereotype = stereotype;
		this.stereotypeClass = stereotypeClass;
		this.stereotypeQualifier = stereotypeQualifier;
	}

	public Class<? extends Annotation> getStereotype() {
		return stereotype;
	}

	public Class<?> getStereotypeClass() {
		return stereotypeClass;
	}

	public Annotation getStereotypeQualifier() {
		return stereotypeQualifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((stereotype == null) ? 0 : stereotype.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StereotypeInfo other = (StereotypeInfo) obj;
		if (stereotype == null) {
			if (other.stereotype != null)
				return false;
		} else if (!stereotype.equals(other.stereotype))
			return false;
		return true;
	}
	
	

}
