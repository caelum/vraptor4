/**
 *
 */
package br.com.caelum.vraptor.util;

import java.util.ListResourceBundle;

import javax.enterprise.inject.Vetoed;

/**
 * Representing an empty resource bundle.
 *
 * @author Lucas Cavalcanti
 */
@Vetoed
public class EmptyBundle extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
		return new Object[0][0];
	}
}