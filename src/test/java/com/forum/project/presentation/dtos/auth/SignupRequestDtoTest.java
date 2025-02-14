package com.forum.project.presentation.dtos.auth;

import com.forum.project.domain.auth.dto.SignupRequestDto;
import com.forum.project.presentation.dtos.messages.ValidationMessages;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
class SignupRequestDtoTest {

    private LocalValidatorFactoryBean validator;

    private void localLogging(BindingResult bindingResult) {
        bindingResult.getFieldErrors().forEach(error -> {
            log.debug("Field: {}", error.getField());
            log.debug("Message: {}", error.getDefaultMessage());
        });
    }

    @BeforeEach
    void setUp() {
        // 메시지 소스 설정
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages_ko"); // messages.properties 파일을 사용
        messageSource.setDefaultEncoding("UTF-8");

        // Validator 설정
        validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messageSource); // 메시지 소스 연결
        validator.afterPropertiesSet(); // 초기화
    }

    private void checkFieldErrorMessages(List<FieldError> fieldErrors, String field, String... expectedMessages) {
        List<String> actualMessages = fieldErrors.stream()
                .filter(e -> e.getField().equals(field))
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        assertThat(actualMessages).containsExactlyInAnyOrder(expectedMessages);
    }

    private BindingResult bindResult(Object dto) {
        DataBinder binder = new DataBinder(dto);
        binder.setValidator(validator);
        binder.validate();

        BindingResult bindingResult = binder.getBindingResult();

        localLogging(bindingResult);
        return bindingResult;
    }

    @Test
    void testSignupRequestDto_Success() {
        SignupRequestDto signupRequestDto = SignupRequestDto.builder()
                .loginId("validUser123")
                .email("email@example.com")
                .password("ValidPass123!")
                .lastName("길동")
                .firstName("홍")
                .build();

        BindingResult bindingResult = bindResult(signupRequestDto);
        assertThat(bindingResult.hasErrors()).isFalse();
    }

    @Test
    void testInvalidSignupRequestDto_UserCase() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("") // 잘못된 값
                .email("invalid-email") // 잘못된 값
                .password("short") // 잘못된 값
                .lastName("1234") // 잘못된 값
                .firstName("") // 잘못된 값
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        checkFieldErrorMessages(fieldErrors, "loginId",
                ValidationMessages.LOGIN_ID_REQUIRED,
                ValidationMessages.LOGIN_ID_LENGTH,
                ValidationMessages.LOGIN_ID_PATTERN);
        checkFieldErrorMessages(fieldErrors, "email",
                ValidationMessages.EMAIL_INVALID);
        checkFieldErrorMessages(fieldErrors, "password",
                ValidationMessages.PASSWORD_LENGTH,
                ValidationMessages.PASSWORD_PATTERN);
        checkFieldErrorMessages(fieldErrors, "firstName",
                ValidationMessages.FIRST_NAME_REQUIRED);
        checkFieldErrorMessages(fieldErrors, "lastName",
                ValidationMessages.LAST_NAME_PATTERN);
    }

    @Test
    void testInvalidSignupRequestDto_AllEmptyInput() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("")
                .email("")
                .password("")
                .lastName("")
                .firstName("")
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        checkFieldErrorMessages(fieldErrors, "loginId",
                ValidationMessages.LOGIN_ID_REQUIRED,
                ValidationMessages.LOGIN_ID_LENGTH,
                ValidationMessages.LOGIN_ID_PATTERN);
        checkFieldErrorMessages(fieldErrors, "email",
                ValidationMessages.EMAIL_REQUIRED);
        checkFieldErrorMessages(fieldErrors, "password",
                ValidationMessages.PASSWORD_REQUIRED,
                ValidationMessages.PASSWORD_LENGTH,
                ValidationMessages.PASSWORD_PATTERN);
        checkFieldErrorMessages(fieldErrors, "firstName",
                ValidationMessages.FIRST_NAME_REQUIRED);
        checkFieldErrorMessages(fieldErrors, "lastName",
                ValidationMessages.LAST_NAME_REQUIRED);
    }

    @Test
    void testInvalidSignupRequestDto_UserIdTooShort() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("usr")
                .email("valid@example.com")
                .password("ValidPass1@")
                .lastName("lastName")
                .firstName("firstName")
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        checkFieldErrorMessages(fieldErrors, "loginId",
                ValidationMessages.LOGIN_ID_LENGTH);
    }

    @Test
    void testInvalidSignupRequestDto_InvalidEmailFormat() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("user123")
                .email("invalid-email")
                .password("ValidPass1@")
                .lastName("lastName")
                .firstName("firstName")
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        checkFieldErrorMessages(fieldErrors, "email",
                ValidationMessages.EMAIL_INVALID);
    }

    @Test
    void testInvalidSignupRequestDto_PasswordTooShort() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("user123")
                .email("user@example.com")
                .password("short")
                .lastName("lastName")
                .firstName("firstName")
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        checkFieldErrorMessages(fieldErrors, "password",
                ValidationMessages.PASSWORD_LENGTH,
                ValidationMessages.PASSWORD_PATTERN);
    }

    @Test
    void testInvalidSignupRequestDto_InvalidName() {
        SignupRequestDto dto = SignupRequestDto.builder()
                .loginId("ValidUser123")
                .email("Validuser@example.com")
                .password("ValidPass1@")
                .lastName("lastName123")
                .firstName("firstName123")
                .build();

        BindingResult bindingResult = bindResult(dto);
        assertThat(bindingResult.hasErrors()).isTrue();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        checkFieldErrorMessages(fieldErrors, "lastName",
                ValidationMessages.LAST_NAME_PATTERN);
        checkFieldErrorMessages(fieldErrors, "firstName",
                ValidationMessages.FIRST_NAME_PATTERN);
    }
}