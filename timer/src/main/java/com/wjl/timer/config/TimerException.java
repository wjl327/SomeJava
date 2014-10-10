package com.wjl.timer.config;

import org.springframework.core.ErrorCoded;

public class TimerException extends Exception implements ErrorCoded {

	private static final long serialVersionUID = 9036224037975032316L;
	
	private String errorCode;

	public TimerException(String errorCode) {
		super();
		setErrorCode(errorCode);
	}

	public TimerException(String errorCode, String message) {
		super(message);
		setErrorCode(errorCode);
	}

	public TimerException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		setErrorCode(errorCode);
	}

	public TimerException(String errorCode, Throwable cause) {
		super(cause);
		setErrorCode(errorCode);
	}
	
	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
 
	public String getErrorCode() {
		return errorCode;
	}
	
	@Override
	public String getMessage() {
		return getErrorCode() + " : " + super.getMessage();
	}

}
