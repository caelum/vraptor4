package br.com.caelum.vraptor.musicjungle.acceptance.infra;

import static org.openqa.selenium.By.name;
import static org.openqa.selenium.By.tagName;

import org.openqa.selenium.WebDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageForm;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageObject;

public class MusicExportPage extends PageObject {

	public MusicExportPage(WebDriver driver) {
		super(driver);
	}

	public void select(String value) {
		PageForm form = new PageForm(driver, tagName("form"));
		form.select(name("_format"), value).submitForm();
	}

}
