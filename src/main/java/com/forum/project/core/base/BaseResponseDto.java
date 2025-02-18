package com.forum.project.core.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseResponseDto {
    private String message;

    public static ResponseEntity<BaseResponseDto> buildSuccessResponse(String message) {
        BaseResponseDto responseBody = new BaseResponseDto(message);
        return ResponseEntity.status(HttpStatus.OK).body(responseBody);
    }
}
