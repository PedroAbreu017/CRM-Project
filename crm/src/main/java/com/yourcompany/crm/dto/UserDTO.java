package com.yourcompany.crm.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    
    @NotBlank(message = "Nome de usuário é obrigatório")
    @Size(min = 4, max = 50, message = "Nome de usuário deve ter entre 4 e 50 caracteres")
    private String username;
    
    private String password;
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;
    
    @NotBlank(message = "Nome é obrigatório")
    private String firstName;
    
    private String lastName;
    
    private boolean enabled = true;
    
    private List<String> roles;
}