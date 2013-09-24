package br.com.caelum.vraptor.core;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@RequestScoped
public class LocalizationProducer {
    
    // for tests only
    @Inject Localization localization;
    
    private Locale locale;
    private ResourceBundle resourceBundle;
    
    @Produces
    public Locale getLocale() {
        if (locale == null) {
            locale = localization.getLocale();
        }
        
        return locale;
    }

    @Produces
    public ResourceBundle getResourceBundle() {
        if (resourceBundle == null) {
            resourceBundle = localization.getBundle();
        }
        
        return resourceBundle;
    }
}
