package org.pethack.lorby.model;

import jakarta.persistence.*;
import lombok.Data;



import java.time.Instant;


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private String password;
    @Column
    private int confirmationCode;
    @Column
    private Instant codeGenerationTime;
    @Column
    private Boolean userConfirmed;


}
