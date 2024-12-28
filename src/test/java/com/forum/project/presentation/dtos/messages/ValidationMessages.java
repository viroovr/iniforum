package com.forum.project.presentation.dtos.messages;

public class ValidationMessages {
    // loginId 관련 메시지
    public static final String LOGIN_ID_REQUIRED = "로그인 아이디는 필수 입력 항목입니다.";
    public static final String LOGIN_ID_LENGTH = "로그인 아이디는 4자 이상, 20자 이하로 입력해야 합니다.";
    public static final String LOGIN_ID_PATTERN = "로그인 아이디는 문자로 시작하고, 문자와 숫자만 포함해야 합니다.";

    // email 관련 메시지
    public static final String EMAIL_REQUIRED = "이메일은 필수 입력 항목입니다.";
    public static final String EMAIL_INVALID = "올바르지 않은 이메일 형식입니다.";

    // password 관련 메시지
    public static final String PASSWORD_REQUIRED = "비밀번호는 필수 입력 항목입니다.";
    public static final String PASSWORD_LENGTH = "비밀번호는 8자 이상, 30자 이하로 입력해야 합니다.";
    public static final String PASSWORD_PATTERN = "비밀번호는 숫자, 문자, 특수문자를 각각 최소 하나 이상 포함해야 합니다.";

    // firstName 관련 메시지
    public static final String FIRST_NAME_REQUIRED = "이름은 필수 입력 항목입니다.";
    public static final String FIRST_NAME_LENGTH = "이름은 최대 50자까지 입력할 수 있습니다.";
    public static final String FIRST_NAME_PATTERN = "이름은 문자만 포함할 수 있습니다.";

    // lastName 관련 메시지
    public static final String LAST_NAME_REQUIRED = "이름은 필수 입력 항목입니다.";
    public static final String LAST_NAME_LENGTH = "이름은 최대 50자까지 입력할 수 있습니다.";
    public static final String LAST_NAME_PATTERN = "이름은 문자만 포함할 수 있습니다.";

}
