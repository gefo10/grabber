package com.grabbler.grabblerapi.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name="users")
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="username", nullable = false, unique = true)
    @Getter @Setter
    private String username;

    @Column(name="email", nullable = false, unique = true)
    @Getter @Setter
    private String email;


    @Column(name="profile_image")
    @Getter @Setter
    private byte[] profileImage;

    @Column(name="mobile_number")
    @Getter @Setter
    private String mobileNumber;

    @Column(name="password_hash", nullable = false)
    @Getter @Setter
    private String passwordHash;

    @Column(name="created_at")
    @Getter @Setter
    private Timestamp created_at;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Message> messages = new HashSet<>();

  
    @Column(name="authorities")
    private Set<GrantedAuthority>authorities = new HashSet<>();
   
    
    
    
    public User() {
    }

    public User(String username, String email, byte[] profileImage, String mobileNumber, String passwordHash, Timestamp created_at) {
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.created_at = created_at;
    }

    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement your logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement your logic
    }
}
