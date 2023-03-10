package ru.itsinfo.springbootsecurityusersbootstrap.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itsinfo.springbootsecurityusersbootstrap.model.Role;
import ru.itsinfo.springbootsecurityusersbootstrap.model.User;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.RolesRepository;
import ru.itsinfo.springbootsecurityusersbootstrap.repository.UsersRepository;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UsersRepository usersRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usersRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", email))
        );
    }

    @Override
    public Iterable<Role> findAllRoles() {
        return rolesRepository.findAll();
    }

    @Override
    public void authenticateOrLogout(Model model, HttpSession session, String authenticationName) {
        if (authenticationName != null) {
            try {
                model.addAttribute("authenticationName", authenticationName);
                session.removeAttribute("Authentication-Name");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<User> findAllUsers() {
        return usersRepository.findAll(Sort.by(Sort.Direction.ASC, "firstName", "lastName"));
    }

    @Override
    public User findUser(Long userId) throws IllegalArgumentException {
        return usersRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException(String.format("User with ID %d not found", userId)));
    }

    @Override
    public void insertUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            String oldPassword = user.getPassword();
            try {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                usersRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                user.setPassword(oldPassword);
                addErrorIfDataIntegrityViolationException(bindingResult);
                addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
            }
        } else {
            addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
        }
    }

    @Override
    public void updateUser(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        bindingResult = checkBindingResultForPasswordField(bindingResult);

        if (!bindingResult.hasErrors()) {
            String oldPassword = user.getPassword();
            try {
                user.setPassword(user.getPassword().isEmpty() ? // todo ???????? ?????? ???????????? ?????????? try
                        findUser(user.getId()).getPassword() :
                        passwordEncoder.encode(user.getPassword()));
                usersRepository.save(user);
            } catch (DataIntegrityViolationException e) {
                user.setPassword(oldPassword);
                addErrorIfDataIntegrityViolationException(bindingResult);
                addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
            }
        } else {
            addRedirectAttributesIfErrorsExists(user, bindingResult, redirectAttributes);
        }
    }

    private void addErrorIfDataIntegrityViolationException(BindingResult bindingResult) {
        bindingResult.addError(new FieldError(bindingResult.getObjectName(),
                "email", "E-mail must be unique"));
    }

    private void addRedirectAttributesIfErrorsExists(User user, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("user", user);
        redirectAttributes.addFlashAttribute("bindingResult", bindingResult);
    }

    /**
     * ?????????????? ????????????, ???????? ?? ?????????????????????????? User ???????????? ???????? password
     *
     * @param bindingResult BeanPropertyBindingResult
     * @return BeanPropertyBindingResult
     */
    private BindingResult checkBindingResultForPasswordField(BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors()) {
            return bindingResult;
        }

        User user = (User) bindingResult.getTarget();
        BindingResult newBindingResult = new BeanPropertyBindingResult(user, bindingResult.getObjectName());
        for (FieldError error : bindingResult.getFieldErrors()) {
            if (!user.isNew() && !error.getField().equals("password")) {
                newBindingResult.addError(error);
            }
        }

        return newBindingResult;
    }

    @Override
    public void deleteUser(Long userId) {
        usersRepository.deleteById(userId);
    }
}
