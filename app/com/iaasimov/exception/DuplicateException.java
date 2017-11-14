package com.iaasimov.exception;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class DuplicateException extends RuntimeException {

	private static final long serialVersionUID = 4329272307461046108L;

	public DuplicateException() {
		super();
	}

	public DuplicateException(String exceptionMessage) {

		super(exceptionMessage);
	}

	public DuplicateException(Throwable e) {
		super(e);
	}

}
