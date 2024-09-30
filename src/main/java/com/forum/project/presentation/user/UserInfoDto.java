package com.forum.project.presentation.user;

import com.forum.project.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String userId;
    private String email;
    private String password;
    private String name;
    private String nickname;
    private LocalDateTime createdDate;
    private String profileImagePath;

    public UserInfoDto(String userId, String email, String password, String name) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserInfoDto(Long id, String userId, String email, String password, String name) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserInfoDto(Long id, String userId, String email) {
        this.id = id;
        this.userId = userId;
        this.email = email;
    }


    static public UserInfoDto toDto(User user) {
        return new UserInfoDto(
                user.getId(),
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getNickname(),
                user.getCreatedDate(),
                user.getProfileImagePath()
        );
    }

    static public User toEntity(UserInfoDto userInfoDto) {
        return new User(
                userInfoDto.getId(),
                userInfoDto.getUserId(),
                userInfoDto.getEmail(),
                userInfoDto.getPassword(),
                userInfoDto.getName(),
                userInfoDto.getCreatedDate(),
                userInfoDto.getProfileImagePath(),
                userInfoDto.getNickname()
        );
    }
}
