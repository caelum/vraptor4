package br.com.caelum.vraptor.serializationx;

public final class Options {

	public static Include include(String... fields) {
		return new Include(fields);
	}

	public static IncludeAll includeAll() {
		return new IncludeAll();
	}

	public static Exclude exclude(String... fields) {
		return new Exclude(fields);
	}

	public static ExcludeAll excludeAll(String... fields) {
		return new ExcludeAll();
	}
}
