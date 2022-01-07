package me.powerarc.flowsubject.fixed;

import org.springframework.stereotype.Service;

import me.powerarc.flowsubject.extension.Extension;
import me.powerarc.flowsubject.extension.ExtensionService;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;

@Service
public class FixedExtensionService extends ExtensionService {
	public FixedExtensionService(FixedExtensionRepository repository) {
		super(repository);
	}

	public ExtensionResponse update(String name, Boolean isChecked) {
		FixedExtension extension = (FixedExtension)get(name);
		extension.setIsChecked(isChecked);
		return ((FixedExtension)repository.save(extension)).toResponse();
	}

	@Override
	public Extension save(String name) {
		return (Extension)repository.save(FixedExtension.builder()
			.name(name)
			.build());
	}
}
