package br.com.caelum.vraptor.musicjungle.acceptance.infra;

import static org.openqa.selenium.By.linkText;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pages.HomePage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.LoginPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.MusicExportPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.ServiceResultPage;

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
		driver.get(getBaseUrl() + "/home/login");
    	return new LoginPage(driver);
    }
	
	public String getBaseUrl() {
		return "http://localhost:8080/vraptor-musicjungle";
	}
	
	public ServiceResultPage accessFullUrl(String uri) {
		driver.get(getBaseUrl() + uri);
		return new ServiceResultPage(driver);
	}

	public MusicExportPage accessMusicsExport() {
		By linkText = linkText("Export all musics");
		driver.findElement(linkText).click();
		return new MusicExportPage(driver);
	}
	
	public HomePage homePage() {
		driver.get(getBaseUrl());
		return new HomePage(driver);
	}
	
}
