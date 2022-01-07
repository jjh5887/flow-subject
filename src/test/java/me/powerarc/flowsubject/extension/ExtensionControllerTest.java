package me.powerarc.flowsubject.extension;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import me.powerarc.flowsubject.config.AppConfig;
import me.powerarc.flowsubject.exception.ExtensionException;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;
import me.powerarc.flowsubject.fixed.FixedExtension;
import me.powerarc.flowsubject.fixed.response.FixedExtensionResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExtensionControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	ExtensionService extensionService;

	@Autowired
	ExtensionRepository<Extension> repository;

	@Autowired
	ApplicationRunner applicationRunner;

	@AfterEach
	public void setUp() throws Exception {
		repository.deleteAll();
		applicationRunner.run(null);
	}

	@Test
	public void getAll() throws Exception {
		List<ExtensionResponse> extensionResponses = extensionService.getAll();
		for (ExtensionResponse extensionResponse : extensionResponses) {
			assertThat(extensionResponse instanceof FixedExtensionResponse).isEqualTo(true);
		}
		assertThat(extensionResponses.size()).isEqualTo(AppConfig.fixedExtensions.length);

		int cnt = 20;
		for (int i = 0; i < cnt; i++) {
			extensionService.create("test" + i);
		}
		extensionResponses = extensionService.getAll();
		assertThat(extensionResponses.size()).isEqualTo(cnt + AppConfig.fixedExtensions.length);

		ResultActions resultActions = mockMvc.perform(get("/extension")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		for (int i = 0; i < extensionResponses.size(); i++) {
			String name = extensionResponses.get(i).getName();
			resultActions.andExpect(jsonPath("$[ " + i + " ].name", is(name)));
		}
	}

	@Test
	public void create() throws Exception {
		String extensionName = "test";
		int totalSize = extensionService.getAll().size();
		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		Extension extension = extensionService.get(extensionName);
		assertThat(extension.getName()).isEqualTo(extensionName);
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize + 1);
		assertThat(extension instanceof FixedExtension).isEqualTo(false);
	}

	@Test
	public void create_duplicated_name() throws Exception {
		List<ExtensionResponse> responses = extensionService.getAll();
		int totalSize = responses.size();
		String extensionName = responses.get(0).getName();

		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);
	}

	@Test
	public void delete() throws Exception {
		List<ExtensionResponse> responses = extensionService.getAll();
		int totalSize = responses.size();
		String extensionName = responses.get(0).getName();

		mockMvc.perform(MockMvcRequestBuilders.delete("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});

		assertThat(extensionService.getAll().size()).isEqualTo(totalSize - 1);
	}

	@Test
	public void delete_not_exists_extension() throws Exception {
		int totalSize = extensionService.getAll().size();
		String extensionName = "null";
		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});

		mockMvc.perform(MockMvcRequestBuilders.delete("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);
	}

}