package com.ws.bebetter.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDto {

    private UUID id;

    private UserDto author;

    @EqualsAndHashCode.Exclude
    @NotBlank(message = "Значение сообщения не может быть пустым.")
    @Length(min = 1, max = 500, message = "Длина сообщения должна составлять от 1 до 500 символов.")
    @Pattern(regexp = ".*\\S.*", message = "Сообщение не может состоять только из пробелов.")
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
