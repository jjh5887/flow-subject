package me.powerarc.flowsubject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import me.powerarc.flowsubject.extension.ExtensionService;
import me.powerarc.flowsubject.fixed.FixedExtensionService;

@Configuration
public class AppConfig {
	public static final String[] fixedExtensions = {
		"bat", "cmd", "com", "cpl", "exe", "scr", "js"
	};

	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {
			@Autowired
			FixedExtensionService fixedExtensionService;

			@Autowired
			ExtensionService extensionService;

			@Override
			public void run(ApplicationArguments args) {
				for (String fixedExtension : fixedExtensions) {
					fixedExtensionService.create(fixedExtension);
				}
			}
		};
	}

	@Bean
	@Profile("!test")
	public ApplicationRunner applicationRunnerForExtension() {
		return new ApplicationRunner() {
			@Autowired
			FixedExtensionService fixedExtensionService;

			@Autowired
			ExtensionService extensionService;

			@Override
			public void run(ApplicationArguments args) {
				for (int i = 0; i < 199; i++) {
					extensionService.create("test" + i);
				}
			}
		};
	}
}
