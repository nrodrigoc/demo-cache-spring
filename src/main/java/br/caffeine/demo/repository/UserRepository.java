package br.caffeine.demo.repository;

import br.caffeine.demo.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {

    Optional<AppUser> findByUsername(String username);
}
