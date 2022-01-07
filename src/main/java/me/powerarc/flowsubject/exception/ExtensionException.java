package me.powerarc.flowsubject.exception;

import lombok.Data;

@Data
public class ExtensionException extends RuntimeException {
	private int status;

	public ExtensionException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
	}
}

