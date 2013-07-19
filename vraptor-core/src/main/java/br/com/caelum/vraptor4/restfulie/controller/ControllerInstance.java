package br.com.caelum.vraptor4.restfulie.controller;

/**
 * Wrapper for controller instance
 * @author Alberto Souza
 *
 */
public interface ControllerInstance {

	public abstract Object getController();

	public abstract BeanClass getBeanClass();

}