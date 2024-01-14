package com.bootravel.common.security.jwt.dto;


import com.bootravel.common.constant.RoleConstants;
import com.bootravel.entity.UsersEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UsersEntity user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = null;
        Long roleId = user.getRoleId();
        if( roleId == 1 ){
            roleName = RoleConstants.ADMIN;
        }
        if( roleId == 2 ){
            roleName = RoleConstants.MARKETING;
        }
        if( roleId == 3 ){
            roleName = RoleConstants.BUSINESS_ADMIN;
        }
        if( roleId == 4 ){
            roleName = RoleConstants.BUSINESS_OWNER;
        }
        if( roleId == 5 ){
            roleName = RoleConstants.BOOKING_STAFF;
        }
        if( roleId == 6 ){
            roleName = RoleConstants.TRANSACTION_STAFF;
        }
        if( roleId == 7 ){
            roleName = RoleConstants.USER;
        }
        return getAuthorities(roleName);
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    public Long getId() {
        return user.getId();
    }

    public long getRoleId() {
        return user.getRoleId();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public String getFullName() {
        return user.getFirstName() + user.getLastName();
    }

    public String getImage(){
        return user.getAvatar();
    }
    private Collection<SimpleGrantedAuthority> getAuthorities(String roles) {
        if (StringUtils.isNotEmpty(roles) && StringUtils.isNotEmpty("/")) {
            String[] roleList = roles.split("/");
            return Arrays.stream(roleList).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
