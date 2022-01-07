package me.powerarc.flowsubject.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	DuplicatedExtension(400, "중복된 확장자 입니다."),
	ExtensionNotFound(400, "없는 확장자 입니다."),
	NameSizeOver(400, "이름이 20자 이상 초과할 수 없습니다."),
	TotalExtensionSizeOver(400, "확장자를 더 이상 늘릴 수 없습니다.");

	private int status;
	private String message;

	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
