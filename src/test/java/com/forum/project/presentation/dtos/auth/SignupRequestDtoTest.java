//package com.forum.project.presentation.dtos.auth;
//
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//class SignupRequestDtoTest {
//
//    private static final Logger logger = Logger.getLogger(SignupRequestDtoTest.class.getName());
//
//    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//    private static final Validator validator = factory.getValidator();
//
//    private void logging(Set<ConstraintViolation<SignupRequestDto>> violations) {
//        if (!violations.isEmpty()) {
//            String errorMessages = violations.stream()
//                    .map(ConstraintViolation::getMessage)  // 오류 메시지 추출
//                    .collect(Collectors.joining("\n"));    // 메시지들을 쉼표로 구분하여 연결
//            logger.info("Validation errors: " + errorMessages);  // 로그로 출력
//        }
//    }
//
//    private void compareExpectedAndActual(Set<String> expectedMessages, Set<ConstraintViolation<SignupRequestDto>> violations) {
//        Set<String> actualMessages = new HashSet<>();
//        for (ConstraintViolation<SignupRequestDto> violation : violations) {
//            actualMessages.add(violation.getMessage());
//        }
//
//        assertTrue(actualMessages.containsAll(expectedMessages));
//    }
//
//    @Test
//    void testValidSignupRequestDto() {
//        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
//                .userId("validUser123")
//                .email("email@example.com")
//                .password("ValidPass123!")
//                .name("홍길동")
//                .build();
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(signupRequestDto);
//
//        assertTrue(violations.isEmpty());
//    }
//
//    @Test
//    void testEmptySignupRequestDto() {
//        SignupRequestDto dto = new SignupRequestDto("", "", "", "");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//
//        Set<String> expectedMessages = new HashSet<>(Arrays.asList(
//                "사용자 ID는 필수입니다.",
//                "이메일은 필수입니다.",
//                "비밀번호는 필수입니다.",
//                "이름은 필수입니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//
//    @Test
//    void testInvalidSignupRequestDto() {
//        SignupRequestDto dto = new SignupRequestDto("1s", "invalid-email", "short", "");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//
//        logging(violations);
//        Set<String> expectedMessages = new HashSet<>(Arrays.asList(
//                "사용자 Id는 최소 4자 이상, 최대 20자 이하이어야 합니다.",
//                "사용자 ID는 알파벳으로 시작해야하며, 알파벳과 숫자만 포함할 수 있습니다.",
//                "유효한 이메일 형식이 아닙니다.",
//                "비밀번호는 최소 1개의 숫자, 1개의 알파벳, 1개의 특수문자를 포함해야 합니다.",
//                "비밀번호는 최소 8자 이상, 30자 이하이어야 합니다.",
//                "이름은 필수입니다.",
//                "이름은 최대 50자 이하이어야 합니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//
//    @Test
//    void testUserIdTooShort() {
//        SignupRequestDto dto = new SignupRequestDto("usr", "valid@example.com", "ValidPass1@", "UserName");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//
//        Set<String> expectedMessages = new HashSet<>(List.of(
//                "사용자 Id는 최소 4자 이상, 최대 20자 이하이어야 합니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//    @Test
//    void testInvalidEmailFormat() {
//        SignupRequestDto dto = new SignupRequestDto("user123", "invalid-email", "ValidPass1@", "User Name");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//
//        Set<String> expectedMessages = new HashSet<>(List.of(
//                "유효한 이메일 형식이 아닙니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//
//    @Test
//    void testPasswordTooShort() {
//        SignupRequestDto dto = new SignupRequestDto("user123", "user@example.com", "short", "User Name");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//        Set<String> expectedMessages = new HashSet<>(Arrays.asList(
//                "비밀번호는 최소 1개의 숫자, 1개의 알파벳, 1개의 특수문자를 포함해야 합니다.",
//                "비밀번호는 최소 8자 이상, 30자 이하이어야 합니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//
//    @Test
//    void testInvalidName() {
//        SignupRequestDto dto = new SignupRequestDto("user123", "user@example.com", "ValidPass1@", "User123");
//
//        Set<ConstraintViolation<SignupRequestDto>> violations = validator.validate(dto);
//
//        Set<String> expectedMessages = new HashSet<>(Arrays.asList(
//                "이름은 한글 또는 영문만 포함할 수 있습니다."
//        ));
//
//        assertFalse(violations.isEmpty());
//        compareExpectedAndActual(expectedMessages, violations);
//    }
//}