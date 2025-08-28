package com.videoclub.filmoapp.auth.dto;

import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMvcDTO {

    private Long id;
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String name;
    private String surname;
    private LocalDate birthDate;
}

