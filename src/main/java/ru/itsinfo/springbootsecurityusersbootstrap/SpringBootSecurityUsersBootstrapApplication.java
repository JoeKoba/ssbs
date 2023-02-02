package ru.itsinfo.springbootsecurityusersbootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itsinfo.springbootsecurityusersbootstrap.model.Role;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.RoleRepository;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.UserRepository;

import java.util.HashSet;

@SpringBootApplication
public class SpringBootSecurityUsersBootstrapApplication {




    public static void main(String[] args) {
        SpringApplication.run(SpringBootSecurityUsersBootstrapApplication.class, args);
    }


}
