package br.com.caelum.vraptor.controller;

public class InvalidInputException extends RuntimeException {

	private static final long serialVersionUID = 5273480707222704252L;

	public InvalidInputException(String message) {
		super(message);
	}
}
