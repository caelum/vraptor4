package br.com.caelum.vraptor.serialization.xstream;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.ioc.cdi.FakeInstanceImpl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * A Simple builder factory for XStreamBuilder tests
 */
public class XStreamBuilderFactory {

	public static XStreamBuilder cleanInstance(Converter...converters) {
		FakeInstanceImpl<Converter> convertersInst = new FakeInstanceImpl<>(converters);
		FakeInstanceImpl<SingleValueConverter> singleValueConverters = new FakeInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		return new XStreamBuilderImpl(xStreamConverters, new DefaultTypeNameExtractor());
	}
}