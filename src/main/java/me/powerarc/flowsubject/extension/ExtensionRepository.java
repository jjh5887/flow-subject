package me.powerarc.flowsubject.extension;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

@Primary
public interface ExtensionRepository<T extends Extension> extends JpaRepository<T, String> {
	Optional<T> findByName(String name);

	Boolean existsByName(String name);
}
