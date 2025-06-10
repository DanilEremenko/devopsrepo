package com.ws.bebetter.web.dto;

import com.ws.bebetter.entity.FileSpecification;
import com.ws.bebetter.util.ValidationUtil;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefDto {
    @NotNull
    private UUID guid;

    @NotNull
    private String mimeType;

    @NotNull
    private String fileName;

    @ValidationUtil.EnumConstraint(enumClass = FileSpecification.class,
            message = "Неподдерживаемое значение спецификации файла")
    @NotNull
    private FileSpecification fileSpecification;
}
