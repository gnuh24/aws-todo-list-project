package com.ec.catalog.exceptions.fileException;

public class EmptyFileUploadException extends RuntimeException {
	public EmptyFileUploadException(String message) {
		super(message);
	}
}
