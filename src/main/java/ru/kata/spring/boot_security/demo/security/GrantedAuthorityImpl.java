package ru.kata.spring.boot_security.demo.security;

import org.springframework.security.core.GrantedAuthority;

public class GrantedAuthorityImpl implements GrantedAuthority {

    private String name;

    public GrantedAuthorityImpl(String name) {
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name;
    }
}
