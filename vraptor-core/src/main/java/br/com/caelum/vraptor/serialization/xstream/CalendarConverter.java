package br.com.caelum.vraptor.serialization.xstream;

import java.util.Calendar;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Calendar} using ISO8601. 
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
@ApplicationScoped
public class CalendarConverter implements Converter {
	
	@Override
	public boolean canConvert(Class type) {
		return Calendar.class.isAssignableFrom(type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		String out = DatatypeConverter.printDateTime((Calendar) source);
		writer.setValue(out);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String value = reader.getValue();
		return DatatypeConverter.parseDateTime(value);
	}
}
