package br.com.caelum.vraptor.musicjungle.acceptance.pageObjects;

import static org.openqa.selenium.By.tagName;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageForm {

	private WebDriver driver;

	public PageForm(WebDriver driver) {
		this.driver = driver;
	}

	public PageForm select(By by, String value) {
		
		WebElement select = driver.findElement(by);
		List<WebElement> options = select.findElements(tagName("option"));
		
		for (WebElement option : options) {
			String optionValue = option.getAttribute("value");
			if (optionValue.equals(value)) option.click();
		}
		return this;
	}

	public void submit() {
		driver.findElement(By.tagName("form")).submit();
	}

}
