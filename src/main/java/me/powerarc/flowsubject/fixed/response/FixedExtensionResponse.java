package me.powerarc.flowsubject.fixed.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FixedExtensionResponse extends ExtensionResponse {
	private Boolean isChecked;
}
