/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.validator.Message;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes {@link Message} object. This class must be in {@link Dependent} to allow us to
 * discover generic type.
 * 
 * @author Otávio Garcia
 * @since 4.0.0
 */
@Dependent
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class MessageGsonConverter implements JsonSerializer<Message> {
	
	@Override
	public JsonElement serialize(Message src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.addProperty("category", src.getCategory());
		json.addProperty("message", src.getMessage());
		
		return json;
	}

}
