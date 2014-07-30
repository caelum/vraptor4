package br.com.caelum.vraptor.serialization;

import javax.enterprise.context.Dependent;

@Dependent
public class Deserializee {

	private boolean withoutRoot;

	public boolean isWithoutRoot() {
		return withoutRoot;
	}

	public void setWithoutRoot(boolean withoutRoot) {
		this.withoutRoot = withoutRoot;
	}
	
}
