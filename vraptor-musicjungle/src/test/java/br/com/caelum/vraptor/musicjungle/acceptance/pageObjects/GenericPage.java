package br.com.caelum.vraptor.musicjungle.acceptance.pageObjects;

import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * A simple page used to get some generic 
 * informations, such as its source code.
 * 
 * @author Rodrigo Turini
 */
public class GenericPage {

	private FirefoxDriver driver;

	public GenericPage(FirefoxDriver driver) {
		this.driver = driver;
	}

	public String pageSource() {
		return this.driver.getPageSource();
	}

}
