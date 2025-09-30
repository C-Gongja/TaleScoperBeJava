package com.TaleScoper.TaleScoper.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterRequest {
	@NotBlank(message = "이메일은 필수입니다")
	@Email(message = "유효한 이메일 형식이어야 합니다")
	private String email;

	@NotBlank(message = "비밀번호는 필수입니다")
	@Size(min = 8, max = 20, message = "비밀번호는 8-20자 사이여야 합니다")
	private String password;

	@NotBlank(message = "이름은 필수입니다")
	@Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
	private String name;

	private String username;
}
