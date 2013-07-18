package br.com.caelum.vraptor4.ioc.cdi;


public interface Code<T> {

	void execute(T bean);

}
