package br.com.caelum.vraptor4;

import br.com.caelum.vraptor.VRaptorException;

public class MustHaveArgumentException extends VRaptorException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3890376636120070072L;

	public MustHaveArgumentException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	public MustHaveArgumentException(String msg, Throwable e) {
		super(msg, e);
		// TODO Auto-generated constructor stub
	}

	public MustHaveArgumentException(Throwable e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	

	
}
