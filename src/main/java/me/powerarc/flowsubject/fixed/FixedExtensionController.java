package me.powerarc.flowsubject.fixed;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.powerarc.flowsubject.extension.ExtensionController;
import me.powerarc.flowsubject.extension.response.ExtensionDeleteResponse;

@RestController
@RequestMapping(value = "/fixed-extension", produces = MediaType.APPLICATION_JSON_VALUE)
public class FixedExtensionController extends ExtensionController {
	public FixedExtensionController(FixedExtensionService extensionService) {
		super(extensionService);
	}

	@PutMapping("/{name}/{isChecked}")
	public ResponseEntity update(@PathVariable String name, @PathVariable Boolean isChecked) {
		((FixedExtensionService)extensionService).update(name, isChecked);
		return ResponseEntity.ok(ExtensionDeleteResponse.builder()
			.message("ok")
			.build());
	}

	@Override
	public ResponseEntity create(String name) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	}

	@Override
	public ResponseEntity delete(String name) {
		return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
	}
}

