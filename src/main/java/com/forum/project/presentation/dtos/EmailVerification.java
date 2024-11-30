package com.forum.project.presentation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerification {
    private String verificationCode;
    private boolean verified;


}
