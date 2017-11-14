package com.iaasimov.exception;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class UnauthorizedException extends RuntimeException {

	// Need to replace this with an appropriate value
	private static final long serialVersionUID = 4329272307461046108L;

	public UnauthorizedException() {
		super();
	}

	public UnauthorizedException(String exceptionMessage) {

		super(exceptionMessage);
	}

	public UnauthorizedException(Throwable e) {
		super(e);
	}

}
