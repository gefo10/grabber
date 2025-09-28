package com.grabbler.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.grabbler.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
