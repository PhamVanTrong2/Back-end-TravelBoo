package com.bootravel.common.security.globalconfig;

import com.bootravel.common.security.jwt.dto.CustomUserDetails;
import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.util.Collection;

@Data
public class AccessAuthentication implements Authentication {

    private CustomUserDetails principal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return principal.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() { // NOSONAR
        return principal;
    }


    @Override
    public boolean isAuthenticated() {
        return principal != null;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return Authentication.super.implies(subject);
    }

    public static AccessAuthentication instanceOf(UserDetails principal) {
        AccessAuthentication authentication = new AccessAuthentication();
        authentication.principal = (CustomUserDetails) principal;
        return authentication;
    }


}
