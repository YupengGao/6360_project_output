package com.ots.service.base;

/**
 * Exception for service layer. 
 */
public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1401593546385403720L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
