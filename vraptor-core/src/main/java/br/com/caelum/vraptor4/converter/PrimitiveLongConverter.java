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

package br.com.caelum.vraptor4.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor4.Convert;
import br.com.caelum.vraptor4.Converter;

/**
 * VRaptor's primitive long converter.
 *
 * @author Cecilia Fernandes
 * @author Guilherme Silveira
 */
@Convert(long.class)
@ApplicationScoped
public class PrimitiveLongConverter implements Converter<Long> {

    public Long convert(String value, Class<? extends Long> type, ResourceBundle bundle) {
        if (isNullOrEmpty(value)) {
            return 0L;
        }
        
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_integer"), value));
        }
    }

}
