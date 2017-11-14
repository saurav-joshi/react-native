package com.iaasimov.exception;

/**
 * 
 * @author HaNguyen modified by $Author: hanguyen $
 * @version $Revision: 1.2 $ 
 */
public class FinderException extends RuntimeException {

	private static final long serialVersionUID = 4329272307461046108L;

	public FinderException() {
		super();
	}

	public FinderException(String exceptionMessage) {

		super(exceptionMessage);
	}

	public FinderException(Throwable e) {
		super(e);
	}

}
