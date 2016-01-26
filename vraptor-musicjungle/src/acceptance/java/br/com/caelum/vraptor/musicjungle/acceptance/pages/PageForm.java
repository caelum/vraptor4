package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import static org.openqa.selenium.By.name;
import static org.openqa.selenium.By.tagName;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PageForm {

	private final WebElement form;

	public PageForm(WebDriver driver, By by) {
		this.form = driver.findElement(by);
	}

	public PageForm select(By by, String value) {
		
		WebElement select = form.findElement(by);
		By byOption = tagName("option");
		List<WebElement> options = select.findElements(byOption);
		
		for (WebElement option : options) {
			String optionValue = option.getAttribute("value");
			if (optionValue.equals(value)) option.click();
		}
		return this;
	}
	
	public PageForm input(String name, String value) {
		WebElement element = form.findElement(name(name));
		element.sendKeys(value);
		return this;
	}

	public void submitForm() {
		form.submit();
	}

}
