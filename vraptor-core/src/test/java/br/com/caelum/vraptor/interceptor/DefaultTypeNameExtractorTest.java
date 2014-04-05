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
package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultTypeNameExtractorTest {


	private DefaultTypeNameExtractor extractor;

	@Before
	public void setUp() throws Exception {
		extractor = new DefaultTypeNameExtractor();
	}
	static class AClass {}

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercased() throws NoSuchMethodException {
		Assert.assertEquals("urlClassLoader",extractor.nameFor(URLClassLoader.class));
		Assert.assertEquals("bigDecimal",extractor.nameFor(BigDecimal.class));
		Assert.assertEquals("string",extractor.nameFor(String.class));
		Assert.assertEquals("aClass",extractor.nameFor(AClass.class));
		Assert.assertEquals("url",extractor.nameFor(URL.class));
	}


	ArrayList<URLClassLoader> urls;
	HashSet<BigDecimal> bigs;
	HashSet<? extends BigDecimal> bigsLimited;
	HashSet<? super BigDecimal> bigsLimited2;
	HashSet<?> objects;
	HashSet bigsOld;
	Vector<String> strings;
	Class<String> clazz;

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercasedForListsAndArrays() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		Assert.assertEquals("stringList",extractor.nameFor(getField("strings")));
		Assert.assertEquals("bigDecimalList",extractor.nameFor(getField("bigs")));
		Assert.assertEquals("hashSet",extractor.nameFor(getField("bigsOld")));
		Assert.assertEquals("class",extractor.nameFor(getField("clazz")));
		Assert.assertEquals("aClassList",extractor.nameFor(AClass[].class));
		Assert.assertEquals("urlClassLoaderList",extractor.nameFor(getField("urls")));
	}

	@Test
	public void shouldDecapitalizeSomeCharsUntilItFindsOneUppercasedForListsAndArraysForBoundedGenericElements() throws NoSuchMethodException, SecurityException, NoSuchFieldException {
		Assert.assertEquals("bigDecimalList",extractor.nameFor(getField("bigsLimited")));
		Assert.assertEquals("bigDecimalList",extractor.nameFor(getField("bigsLimited2")));
		Assert.assertEquals("objectList",extractor.nameFor(getField("objects")));
	}
	@Test
	public void shouldDiscoverGenericTypeParametersWhenThereIsInheritance() throws Exception {
		Assert.assertEquals("t",extractor.nameFor(XController.class.getMethod("edit").getGenericReturnType()));
		Assert.assertEquals("tList",extractor.nameFor(XController.class.getMethod("list").getGenericReturnType()));
	}

	static class Generic<T> {
		public T edit() {
			return null;
		}
		public List<T> list() {
			return null;
		}
	}

	static class XController extends Generic<String> {

	}

	private Type getField(String string) throws SecurityException, NoSuchFieldException {
		return this.getClass().getDeclaredField(string).getGenericType();
	}

}
