package me.powerarc.flowsubject.extension;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.MethodNotAllowedException;

import lombok.RequiredArgsConstructor;
import me.powerarc.flowsubject.extension.response.ExtensionDeleteResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/extension", produces = MediaType.APPLICATION_JSON_VALUE)
public class ExtensionController {
	protected final ExtensionService extensionService;

	@GetMapping
	public ResponseEntity getAll() {
		return ResponseEntity.ok(extensionService.getAll());
	}

	@PostMapping("/{name}")
	public ResponseEntity create(@PathVariable String name) throws MethodNotAllowedException {
		return ResponseEntity.ok(extensionService.create(name));
	}

	@DeleteMapping("/{name}")
	public ResponseEntity delete(@PathVariable String name) {
		extensionService.delete(name);
		return ResponseEntity.ok(ExtensionDeleteResponse.builder()
			.message("ok")
			.build());
	}
}
