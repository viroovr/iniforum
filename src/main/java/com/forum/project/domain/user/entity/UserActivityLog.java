package com.forum.project.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

@Data
@Document(indexName = "user_activity_logs")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActivityLog {
    @Id
    private String id;
    private Long userId;
    private String action; // 수행한 활동 (예: 로그인, 글 작성, 댓글 삭제 등)
    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private Instant timestamp; // 활동 시간
    private String additionalInfo; // 추가 정보 (예: 요청 IP, 브라우저 정보 등)
}
