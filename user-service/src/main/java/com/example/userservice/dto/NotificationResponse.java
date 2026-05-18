package com.example.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;

    /** Ensures JSON key is "isRead" (Jackson can emit "read" for boolean getters otherwise). */
    @JsonProperty("isRead")
    private boolean isRead;

    private java.time.Instant createdDate;
}
