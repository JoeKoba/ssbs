package ru.itsinfo.springbootsecurityusersbootstrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<UserDetails> findByEmail(String email);
}
