package com.videoclub.filmoapp.auth.repository;

import com.videoclub.filmoapp.auth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleDAO extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
