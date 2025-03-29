package com.yourcompany.crm.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourcompany.crm.dto.UserDTO;
import com.yourcompany.crm.model.Role;
import com.yourcompany.crm.model.Role.RoleType;
import com.yourcompany.crm.model.User;
import com.yourcompany.crm.repository.RoleRepository;
import com.yourcompany.crm.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Verificar se o usuário já existe
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Nome de usuário já está em uso");
        }
        
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email já está em uso");
        }
        
        // Criar novo usuário
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setActive(true);
        
        // Atribuir roles
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            HashSet<Role> roles = new HashSet<>();
            for (String roleName : userDTO.getRoles()) {
                try {
                    RoleType roleType = RoleType.valueOf(roleName);
                    Optional<Role> role = roleRepository.findByName(roleType);
                    role.ifPresent(roles::add);
                } catch (IllegalArgumentException e) {
                    log.warn("Role inválida: {}", roleName);
                }
            }
            user.setRoles(roles);
        } else {
            // Atribuir role padrão se nenhuma for especificada
            HashSet<Role> roles = new HashSet<>();
            Optional<Role> defaultRole = roleRepository.findByName(RoleType.ROLE_USER);
            defaultRole.ifPresent(roles::add);
            user.setRoles(roles);
        }
        
        user = userRepository.save(user);
        log.info("Usuário criado: {}", user.getUsername());
        
        return toDTO(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Atualizar campos permitidos
        user.setName(userDTO.getFirstName() + " " + userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        
        // Atualizar senha se fornecida
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Atualizar roles se fornecidas
        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            HashSet<Role> roles = new HashSet<>();
            for (String roleName : userDTO.getRoles()) {
                try {
                    RoleType roleType = RoleType.valueOf(roleName);
                    Optional<Role> role = roleRepository.findByName(roleType);
                    role.ifPresent(roles::add);
                } catch (IllegalArgumentException e) {
                    log.warn("Role inválida: {}", roleName);
                }
            }
            user.setRoles(roles);
        }
        
        user = userRepository.save(user);
        log.info("Usuário atualizado: {}", user.getUsername());
        
        return toDTO(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        userRepository.delete(user);
        log.info("Usuário removido: {}", user.getUsername());
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        // Verificar senha atual
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Senha atual incorreta");
        }
        
        // Atualizar para nova senha
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Senha alterada para o usuário: {}", user.getUsername());
    }
    
    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        user.setActive(!user.getActive());
        userRepository.save(user);
        log.info("Status do usuário alterado: {} -> {}", user.getUsername(), user.getActive() ? "ativo" : "inativo");
    }
    
    @Transactional(readOnly = true)
    public List<UserDTO> findUsersByRole(RoleType roleType) {
        List<User> users = userRepository.findByRolesName(roleType);
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        
        // Tentando extrair primeiro e último nome
        String[] nameParts = user.getName().split(" ", 2);
        dto.setFirstName(nameParts[0]);
        dto.setLastName(nameParts.length > 1 ? nameParts[1] : "");
        
        dto.setEnabled(user.getActive());
        
        // Não incluir a senha no DTO por segurança
        dto.setPassword(null);
        
        // Incluir funções/roles
        if (user.getRoles() != null) {
            dto.setRoles(user.getRoles().stream()
                    .map(role -> role.getName().toString())
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}