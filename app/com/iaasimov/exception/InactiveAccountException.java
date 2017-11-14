package com.iaasimov.exception;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class InactiveAccountException extends RuntimeException {

	private static final long serialVersionUID = 4329272307461046108L;

	public InactiveAccountException() {
		super();
	}

	public InactiveAccountException(String exceptionMessage) {

		super(exceptionMessage);
	}

	public InactiveAccountException(Throwable e) {
		super(e);
	}

}
