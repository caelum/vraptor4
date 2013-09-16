package br.com.caelum.vraptor.ioc.cdi;


public interface Code<T> {

	void execute(T bean);

}
