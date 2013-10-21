package br.com.caelum.vraptor.controller;

/**
 * Wrapper for controller instance
 * @author Alberto Souza
 *
 */
public interface ControllerInstance {

	Object getController();

	BeanClass getBeanClass();

}