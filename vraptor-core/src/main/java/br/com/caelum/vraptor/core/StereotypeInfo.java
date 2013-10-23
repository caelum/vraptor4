package br.com.caelum.vraptor.core;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class StereotypeInfo {

	private final Class<? extends Annotation> stereotype;
	private final Class<?> stereotypeClass;
	private final Annotation stereotypeQualifier;

	public StereotypeInfo(Class<? extends Annotation> stereotype, 
			Class<?> stereotypeClass, Annotation stereotypeQualifier) {
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
		return Objects.hash(stereotype);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		StereotypeInfo other = (StereotypeInfo) obj;
		return Objects.equals(stereotype, other.stereotype);
	}
}
