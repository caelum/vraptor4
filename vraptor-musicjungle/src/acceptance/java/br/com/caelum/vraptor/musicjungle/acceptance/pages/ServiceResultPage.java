package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import org.openqa.selenium.WebDriver;

public class ServiceResultPage {

	private WebDriver driver;

	public ServiceResultPage(WebDriver driver) {
		this.driver = driver;
	}

	public String pageSource() {
		return this.driver.getPageSource();
	}
	
}
