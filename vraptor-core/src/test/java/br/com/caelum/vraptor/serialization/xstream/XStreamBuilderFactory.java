package br.com.caelum.vraptor.serialization.xstream;

import javax.enterprise.inject.Instance;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * A Simple builder factory for XStreamBuilder tests
 */
public class XStreamBuilderFactory {

	public static XStreamBuilder cleanInstance(Converter...converters) {
		Instance<Converter> convertersInst = new MockInstanceImpl<>(converters);
		Instance<SingleValueConverter> singleValueConverters = new MockInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		return new XStreamBuilderImpl(xStreamConverters, new DefaultTypeNameExtractor());
	}
}