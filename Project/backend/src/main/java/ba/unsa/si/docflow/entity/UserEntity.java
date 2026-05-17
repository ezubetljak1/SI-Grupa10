package ba.unsa.si.docflow.entity;

import ba.unsa.si.docflow.entity.enums.AccountStatus;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "app_user",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_company_email",
                    columnNames = {"company_id", "email"}),
            @UniqueConstraint(
                    name = "uk_user_keycloak_user_id",
                    columnNames = {"keycloak_user_id"})
        })
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "keycloak_user_id", nullable = false, length = 255)
    private String keycloakUserId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 50)
    private AccountStatus accountStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
