package me.powerarc.flowsubject.fixed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.powerarc.flowsubject.extension.Extension;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;
import me.powerarc.flowsubject.fixed.response.FixedExtensionResponse;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FixedExtension extends Extension {
	@Column(nullable = false)
	private Boolean isChecked;

	@PrePersist
	public void setUp() {
		this.isChecked = false;
	}

	@Override
	public ExtensionResponse toResponse() {
		FixedExtensionResponse build = FixedExtensionResponse.builder()
			.name(this.name)
			.isChecked(this.isChecked)
			.build();
		return build;
	}
}
