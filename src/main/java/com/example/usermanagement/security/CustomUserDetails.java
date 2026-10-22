package com.example.usermanagement.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails extends User {

    private final String fullName;
    private final String email;

    public CustomUserDetails(String username,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         String fullName, String email){
        super(username, password, authorities);
        this.fullName = fullName;
        this.email = email;
    }
}
