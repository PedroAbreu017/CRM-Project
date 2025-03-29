package com.yourcompany.crm.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String refreshToken;  // Adicionando campo para o refresh token

    // Construtor básico para resposta simples de token
    public JwtResponse(String token) {
        this.token = token;
        this.type = "Bearer";
    }
    
    // Construtor completo para resposta com dados do usuário
    public JwtResponse(String token, Long id, String username, String email, String refreshToken) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.refreshToken = refreshToken;
    }
}