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
		// 초기 고정 확장자 확인
		List<ExtensionResponse> extensionResponses = extensionService.getAll();
		for (ExtensionResponse extensionResponse : extensionResponses) {
			assertThat(extensionResponse instanceof FixedExtensionResponse).isEqualTo(true);
		}
		assertThat(extensionResponses.size()).isEqualTo(AppConfig.fixedExtensions.length);

		// When
		// 커스텀 확장자 20개 생성
		int cnt = 20;
		for (int i = 0; i < cnt; i++) {
			extensionService.create("test" + i);
		}

		// Then
		extensionResponses = extensionService.getAll();
		assertThat(extensionResponses.size()).isEqualTo(cnt + AppConfig.fixedExtensions.length);

		// When
		// 전체 확장자 조회
		ResultActions resultActions = mockMvc.perform(get("/extension")
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		// Then
		for (int i = 0; i < extensionResponses.size(); i++) {
			String name = extensionResponses.get(i).getName();
			resultActions.andExpect(jsonPath("$[ " + i + " ].name", is(name)));
		}
	}

	@Test
	public void create() throws Exception {
		// When
		// test 확장자 생성
		String extensionName = "test";
		int totalSize = extensionService.getAll().size();
		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		// Then
		Extension extension = extensionService.get(extensionName);
		assertThat(extension.getName()).isEqualTo(extensionName);
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize + 1);
		assertThat(extension instanceof FixedExtension).isEqualTo(false);
	}

	@Test
	public void create_too_long_name() throws Exception {
		// When
		// 이름이 긴 확장자 생성
		String extensionName = "testasdasdasdasdasdasdasdasdasd";
		int totalSize = extensionService.getAll().size();
		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Then
		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);
	}

	@Test
	public void create_too_many_extension() throws Exception {
		// When
		// 커스텀 확장자 200개 생성
		String extensionName = "test";
		for (int i = 0; i < 200; i++) {
			extensionService.create(extensionName + i);
		}
		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Then
		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});

		// 고정 확장자의 갯수는 제외
		assertThat(extensionService.getAll().size()).isEqualTo(200 + AppConfig.fixedExtensions.length);
	}

	@Test
	public void create_duplicated_name() throws Exception {
		// When
		// 중복 확장자 생성(고정 확장자 포함)
		List<ExtensionResponse> responses = extensionService.getAll();
		int totalSize = responses.size();
		String extensionName = responses.get(0).getName();

		mockMvc.perform(post("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Then
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);

		// When
		// 커스텀 확장자 생성
		ExtensionResponse extensionResponse = extensionService.create("test");

		// Then
		mockMvc.perform(post("/extension/" + extensionResponse.getName())
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void delete() throws Exception {
		// When
		int totalSize = extensionService.getAll().size();
		// 커스텀 확장자 추가
		String extensionName = extensionService.create("test").getName();
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize + 1);
		assertThat(extensionService.get(extensionName).getName()).isEqualTo(extensionName);
		totalSize += 1;

		// 커스텀 확장자 삭제
		mockMvc.perform(MockMvcRequestBuilders.delete("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());

		// Then
		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize - 1);
	}

	@Test
	public void delete_fixed_extension() throws Exception {
		// When
		// 고정 확장자
		List<ExtensionResponse> extensionResponses = extensionService.getAll();
		int totalSize = extensionResponses.size();
		String extensionName = extensionResponses.get(0).getName();

		// 고정 확장자 삭제
		mockMvc.perform(MockMvcRequestBuilders.delete("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Then
		assertDoesNotThrow(() -> {
			extensionService.get(extensionName);
		});
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);
	}

	@Test
	public void delete_not_exists_extension() throws Exception {
		// When
		// 없는 확장자
		int totalSize = extensionService.getAll().size();
		String extensionName = "null";
		assertThrows(ExtensionException.class, () -> {
			extensionService.get(extensionName);
		});

		// 없는 확장자 삭제
		mockMvc.perform(MockMvcRequestBuilders.delete("/extension/" + extensionName)
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());

		// Then
		assertThat(extensionService.getAll().size()).isEqualTo(totalSize);
	}

}