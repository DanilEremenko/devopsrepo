package com.ws.bebetter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "specialist_feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "manager_id", referencedColumnName = "id")
    private User author;

}
