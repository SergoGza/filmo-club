package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDAO extends JpaRepository<Role, Long> {
}
