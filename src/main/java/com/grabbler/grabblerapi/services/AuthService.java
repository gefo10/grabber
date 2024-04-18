package com.grabbler.grabblerapi.services;

import org.springframework.security.config.annotation.authentication.configurers.userdetails.UserDetailsServiceConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService { 
    @Autowired
    private UserDetailsService userdetailsService;

    //@Autowired
    //private final KeyStoreManager keyStoreManager;
    @Autowired
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    //private final JwtUtil jwtUtil;



}
