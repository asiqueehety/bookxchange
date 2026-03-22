package com.example.bookxchange.dto;

import com.example.bookxchange.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String uid;
    private String username;
    private String userEmail;
    private String profilePic;
    private UserRole userRole;
    private LocalDateTime dateJoined;
}
