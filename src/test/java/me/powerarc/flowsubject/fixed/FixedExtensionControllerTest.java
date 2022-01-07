package me.powerarc.flowsubject.fixed;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import me.powerarc.flowsubject.extension.response.ExtensionResponse;
import me.powerarc.flowsubject.fixed.response.FixedExtensionResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FixedExtensionControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Autowired
	FixedExtensionService fixedExtensionService;

	@Test
	public void create() throws Exception {
		// 고정 확장자 생성 허용 X
		mockMvc.perform(post("/fixed-extension/testtest"))
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void delete() throws Exception {
		// 고정 확장자 삭제 허용 X
		mockMvc.perform(MockMvcRequestBuilders.delete("/fixed-extension/testtest"))
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void update() throws Exception {
		// When
		// 고정 확장자
		List<ExtensionResponse> responses = fixedExtensionService.getAll();
		for (ExtensionResponse response : responses) {
			FixedExtensionResponse fixedExtensionResponse = (FixedExtensionResponse)response;
			assertThat(fixedExtensionResponse.getIsChecked()).isEqualTo(false);
		}
		ExtensionResponse response = responses.get(0);

		// 고정확장자 true 로 변경
		mockMvc.perform(put("/fixed-extension/" + response.getName() + "/true"))
			.andDo(print())
			.andExpect(status().isOk());

		// Then
		FixedExtension extension = (FixedExtension)fixedExtensionService.get(response.getName());
		assertThat(extension.getIsChecked()).isEqualTo(true);
	}

}