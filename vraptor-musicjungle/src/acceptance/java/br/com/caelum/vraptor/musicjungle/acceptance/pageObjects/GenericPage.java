package br.com.caelum.vraptor.musicjungle.acceptance.pageObjects;

import org.openqa.selenium.WebDriver;

/**
 * A simple page used to get some generic 
 * informations, such as its source code.
 * 
 * @author Rodrigo Turini
 */
public class GenericPage {

	private WebDriver driver;

	public GenericPage(WebDriver driver) {
		this.driver = driver;
	}

	public String pageSource() {
		return this.driver.getPageSource();
	}
	
	public WebDriverForm getForm() {
		return new WebDriverForm(this.driver);
	}

}
