package com.ws.bebetter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "photo_ref_id")
    private FileRef photo;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "work_experience")
    private String workExperience;

    @OneToOne
    @JoinColumn(name = "active_role_id")
    private UserRole activeRole;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Column(name = "active_status")
    private Boolean activeStatus = true;

}
