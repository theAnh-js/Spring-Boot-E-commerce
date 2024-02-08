package com.shopme.common.exception;

public class CategoryNotFoundException extends Exception {

	private static final long serialVersionUID = 3172525444188724286L;

	public CategoryNotFoundException(String message) {
		super(message);
	}
}
