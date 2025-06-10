package com.ws.bebetter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@Table(name = "methodologist_details")
@NoArgsConstructor
@AllArgsConstructor
public class MethodologistDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "is_duty_mentor")
    private Boolean isDutyMentor;

    @OneToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;

}
