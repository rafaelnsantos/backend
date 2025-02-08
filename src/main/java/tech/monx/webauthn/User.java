package tech.monx.webauthn;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Table(name = "user_table")
@Entity
public class User extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(unique = true)
    public String username;

    public User () {
        this.id = UUID.randomUUID();
    }

    public User (String username) {
        this();
        this.username = username;
    }

    // non-owning side, so we can add more credentials later
    @OneToOne(mappedBy = "user")
    public WebAuthnCredential webAuthnCredential;

    public static User findByUsername(String username) {
        return User.find("username", username).firstResult();
    }
}