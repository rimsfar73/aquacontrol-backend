package com.example.aquacontrol_backend;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashesBCrypt {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("OPERARIO: " + encoder.encode("demo1234"));
        System.out.println("SUPERVISOR: " + encoder.encode("demo5678"));
    }
}