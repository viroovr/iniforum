package com.forum.project.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Data
@Document(indexName = "user-activity-log")
@Builder
public class UserActivityLog {
    @Id
    private Long id;
    private Long userId;
    private String action; // 수행한 활동 (예: 로그인, 글 작성, 댓글 삭제 등)
    private LocalDateTime timestamp; // 활동 시간
    private String additionalInfo; // 추가 정보 (예: 요청 IP, 브라우저 정보 등)
}
