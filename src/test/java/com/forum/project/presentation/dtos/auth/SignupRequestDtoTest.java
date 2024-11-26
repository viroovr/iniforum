package com.forum.project.presentation.dtos.auth;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SignupRequestDtoTest {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private SignupRequestDto signupRequestDto;

    @Test
    void testValidSignupRequestDto() {
        signupRequestDto = SignupRequestDto.builder()
                .userId("validUser123")
                .email("email@example.com")
                .password("ValidPass123!")
                .name("홍길동")
                .build();

        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptySignupRequestDto() {
        SignupRequestDto dto = new SignupRequestDto("", "", "", "");

        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);

        System.out.println(violations);

        assertFalse(violations.isEmpty());

        assertEquals(8, violations.size());
    }

    @Test
    void testInvalidSignupRequestDto() {
        // 잘못된 입력값을 갖는 SignupRequestDto 생성
        SignupRequestDto dto = new SignupRequestDto("1s", "invalid-email", "short", "");

        // 유효성 검사
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);

        // 검증 오류가 있어야 함
        assertFalse(violations.isEmpty());

        // 예상하는 오류 메시지 개수 확인 (각 필드에서 발생할 오류가 있어야 함)
        assertEquals(6, violations.size()); // 각 필드에서 1개의 오류가 발생해야 함
    }


    @Test
    void testUserIdTooShort() {
        SignupRequestDto dto = new SignupRequestDto("usr", "valid@example.com", "ValidPass1@", "User Name");

        // 유효성 검사
        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);

        // 사용자 ID가 너무 짧으면 오류가 있어야 함
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("사용자 Id는 최소 4자 이상, 최대 20자 이하이어야 합니다.", violations.iterator().next().getMessage());
    }


}