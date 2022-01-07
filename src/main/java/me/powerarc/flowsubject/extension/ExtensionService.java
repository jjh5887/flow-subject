package me.powerarc.flowsubject.extension;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.powerarc.flowsubject.config.AppConfig;
import me.powerarc.flowsubject.exception.ErrorCode;
import me.powerarc.flowsubject.exception.ExtensionException;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class ExtensionService {
	protected final ExtensionRepository repository;

	public ExtensionResponse create(String name) {
		if (repository.count() >= 200 + AppConfig.fixedExtensions.length) {
			throw new ExtensionException(ErrorCode.TotalExtensionSizeOver);
		}
		if (name.length() > 20) {
			throw new ExtensionException(ErrorCode.NameSizeOver);
		}
		if (exists(name)) {
			throw new ExtensionException(ErrorCode.DuplicatedExtension);
		}
		return save(name).toResponse();
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
		repository.delete(get(name));
	}
}
