package br.com.caelum.vraptor.musicjungle.validation.impl;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import br.com.caelum.vraptor.musicjungle.dao.repository.Users;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.musicjungle.validation.LoginAvailable;

public class LoginAvailableValidator
    implements ConstraintValidator<LoginAvailable, User> {
    
    @Inject
    private Users users;

    @Override
    public void initialize(LoginAvailable constraintAnnotation) {

    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        return users.load(user) == null;
    }
}
