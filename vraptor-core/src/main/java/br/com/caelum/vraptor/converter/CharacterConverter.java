/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

/**
 * VRaptor's Character converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Character.class)
@ApplicationScoped
public class CharacterConverter implements Converter<Character> {
    
    private ResourceBundle bundle;
    
    @Deprecated
    public CharacterConverter() {
    }

    @Inject
    public CharacterConverter(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public Character convert(String value, Class<? extends Character> type) {
        if (isNullOrEmpty(value)) {
            return null;
        }

        if (value.length() != 1) {
			throw new ConversionException(MessageFormat.format(bundle.getString("is_not_a_valid_character"), value));
        }

        return value.charAt(0);
    }

}
