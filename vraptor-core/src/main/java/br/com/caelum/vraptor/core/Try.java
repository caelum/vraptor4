package br.com.caelum.vraptor.core;

import java.util.concurrent.Callable;

/**
 * A class to wrap code that can possibly throw exceptions.
 *
 * Use the static method Try#run to instantiate this class, passing down
 * the dangerous code and use its methods to retrieve the result or the exception
 * of your computation. 
 *
 * @author Chico Sokol
 * @param <T> the type of the result of your computation
 */
public abstract class Try<T> {
	public static <T> Try run(Callable<T> callable) {
		try {
			T call = callable.call();
			return new Success(call);
		} catch (Exception e) {
			return new Failed(e);
		}
	}

	public abstract boolean failed();

	public abstract T result();

	public abstract Exception getException();

	public static class Success<T> extends Try {
		private final T result;

		private Success(T result) {
			this.result = result;
		}

		@Override
		public boolean failed() {
			return false;
		}

		@Override
		public Object result() {
			return result;
		}

		@Override
		public Exception getException() {
			throw new UnsupportedOperationException("A Success doesn't have an exception.");
		}
	}

	public static class Failed extends Try {
		private final Exception e;

		private Failed(Exception e) {
			this.e = e;
		}

		@Override
		public boolean failed() {
			return true;
		}

		@Override
		public Object result() {
			throw new UnsupportedOperationException("A Failed doesn't have a result.");
		}

		@Override
		public Exception getException() {
			return e;
		}
	}

}
