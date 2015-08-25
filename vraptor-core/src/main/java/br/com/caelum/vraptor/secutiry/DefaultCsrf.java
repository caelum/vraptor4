package br.com.caelum.vraptor.secutiry;

import java.io.Serializable;
import java.util.UUID;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 * Default implementation of {@link Csrf} interface.
 *
 * @author Rodrigo Turini
 * @since 4.2
 */
@Named("csrf") 
@SessionScoped
public class DefaultCsrf implements Csrf, Serializable {

	private static final long serialVersionUID = -8956544291675282667L;
	
	private UUID token = UUID.randomUUID();

    @Override
	public String getName() {
        return Csrf.CSRF_HEADER;
    }

    @Override
	public String getToken() {
        return token.toString();
    }
}