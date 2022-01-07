package me.powerarc.flowsubject.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.powerarc.flowsubject.config.AppConfig;
import me.powerarc.flowsubject.exception.ErrorCode;
import me.powerarc.flowsubject.exception.ExtensionException;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;
import me.powerarc.flowsubject.fixed.FixedExtension;

@Service
@Transactional
@RequiredArgsConstructor
public class ExtensionService {
	protected final ExtensionRepository repository;

	public ExtensionResponse create(String name) {
		validate(name);
		return save(name).toResponse();
	}

	public void validate(String name) {
		name = name.toLowerCase();
		if (repository.count() >= 200 + AppConfig.fixedExtensions.length) {
			throw new ExtensionException(ErrorCode.TotalExtensionSizeOver);
		}
		if (name.length() > 20) {
			throw new ExtensionException(ErrorCode.NameSizeOver);
		}
		if (exists(name)) {
			throw new ExtensionException(ErrorCode.DuplicatedExtension);
		}
		if (name.contains(" ")) {
			throw new ExtensionException(ErrorCode.WrongName);
		}
		char[] arr = name.toCharArray();
		if (!Character.isAlphabetic(arr[0])) {
			throw new ExtensionException(ErrorCode.WrongName);
		}
		for (int i = 0; i < arr.length; i++) {
			if (!Pattern.matches("^[a-zA-Z0-9]*$", name)) {
				throw new ExtensionException(ErrorCode.WrongName);
			}
		}
		for (char c : arr) {
			if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
				throw new ExtensionException(ErrorCode.WrongName);
			}
		}
	}

	public Extension save(String name) {
		return (Extension)repository.save(Extension.builder().name(name).build());
	}

	@Transactional(readOnly = true)
	public boolean exists(String name) {
		return repository.existsByName(name);
	}

	@SneakyThrows
	@Transactional(readOnly = true)
	public Extension get(String name) {
		return (Extension)repository.findByName(name).orElseThrow(() -> {
			throw new ExtensionException(ErrorCode.ExtensionNotFound);
		});
	}

	@SneakyThrows
	@Transactional(readOnly = true)
	public List<ExtensionResponse> getAll() {
		List<ExtensionResponse> list = new ArrayList<>();
		for (Object extension : repository.findAll(Sort.by("id"))) {
			list.add(((Extension)extension).toResponse());
		}
		return list;
	}

	public void delete(String name) {
		Extension extension = get(name);
		if (extension instanceof FixedExtension) {
			throw new ExtensionException(ErrorCode.DeleteFixedExtensionNotAllowed);
		}
		repository.delete(extension);
	}
}
