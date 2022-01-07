package me.powerarc.flowsubject.extension;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import me.powerarc.flowsubject.extension.response.ExtensionResponse;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "extension_type")
public class Extension {
	@Id
	@GeneratedValue
	private Long id;

	@Column(length = 20, unique = true)
	protected String name;

	public ExtensionResponse toResponse() {
		return ExtensionResponse.builder()
			.name(this.name)
			.build();
	}
}
