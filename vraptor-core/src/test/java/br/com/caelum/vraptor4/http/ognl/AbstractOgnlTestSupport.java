package br.com.caelum.vraptor4.http.ognl;

import java.util.List;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import br.com.caelum.vraptor4.core.Converters;
import br.com.caelum.vraptor4.proxy.JavassistProxifier;
import br.com.caelum.vraptor4.proxy.Proxifier;
import br.com.caelum.vraptor4.proxy.ReflectionInstanceCreator;

public final class AbstractOgnlTestSupport {

	public static void configOgnl(Converters converters) throws OgnlException {
	    Proxifier proxifier = new JavassistProxifier(new ReflectionInstanceCreator());
		OgnlRuntime.setNullHandler(Object.class, new ReflectionBasedNullHandler(proxifier));

		OgnlRuntime.setPropertyAccessor(List.class, new ListAccessor(converters));

		OgnlRuntime.setPropertyAccessor(Object[].class, new ArrayAccessor());
	}

}