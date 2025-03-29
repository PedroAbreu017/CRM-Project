package com.yourcompany.crm.repository;

import com.yourcompany.crm.model.Role;
import com.yourcompany.crm.model.Role.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleType name);
}