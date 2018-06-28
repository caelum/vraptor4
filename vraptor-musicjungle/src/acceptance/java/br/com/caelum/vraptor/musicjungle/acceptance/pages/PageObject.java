package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import org.openqa.selenium.WebDriver;

public class PageObject {

	protected final WebDriver driver;

	public PageObject(WebDriver driver) {
		this.driver = driver;
	}

	public String pageSource() {
		return driver.getPageSource();
	}

}
