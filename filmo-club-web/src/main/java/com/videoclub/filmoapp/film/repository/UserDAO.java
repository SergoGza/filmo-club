package com.videoclub.filmoapp.film.repository;

import com.videoclub.filmoapp.film.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Long> {
}
