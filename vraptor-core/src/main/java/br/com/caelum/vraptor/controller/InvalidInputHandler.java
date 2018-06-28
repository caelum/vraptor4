package br.com.caelum.vraptor.controller;

/**
 * Handler for Bad Request (400).
 * 
 * @author Rodrigo Turini
 */
public interface InvalidInputHandler {

	void deny(InvalidInputException e);
}
