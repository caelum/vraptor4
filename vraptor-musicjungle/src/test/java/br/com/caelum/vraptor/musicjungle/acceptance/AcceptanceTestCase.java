package br.com.caelum.vraptor.musicjungle.acceptance;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pageObjects.GenericPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pageObjects.LoginPage;

public abstract class AcceptanceTestCase {

	private static WebDriver driver;

	@BeforeClass
	public static void beforeClass() {
		driver = new FirefoxDriver();
	}
	
	@AfterClass
	public static void afterShutdown() {
		if (driver != null) driver.close();
	}
	
	public LoginPage loginPage() {
		driver.get(getBaseUrl());
    	return new LoginPage(driver);
    }
	
	public String getBaseUrl() {
		return "http://localhost:8080/vraptor-musicjungle";
	}
	
	public GenericPage accessFullUrl(String uri) {
		driver.get(getBaseUrl() + uri);
		return new GenericPage(driver);
	}
}
