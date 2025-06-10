package com.ws.bebetter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Table(name = "files_ref")
public class FileRef {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_specification")
    private FileSpecification fileSpecification;
}
