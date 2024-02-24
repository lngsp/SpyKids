package ro.spykids.server.services;

import ro.spykids.server.jwt.JwtService;
import ro.spykids.server.exceptions.NoAreaFoundExceptions;
import ro.spykids.server.exceptions.NoChildFoundException;
import ro.spykids.server.exceptions.UserAlreadyExistsException;
import ro.spykids.server.model.User;
import ro.spykids.server.repository.TokenRepository;
import ro.spykids.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final TokenRepository tokenRepository;

    public User save(User user){

        log.info("Saving new user {} to the database.", user.getEmail());
        userRepository.save(user);
        return user;
    }

    public User getUserById(@PathVariable Integer id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("There is no account with this id."));

        return user;
    }

    public Optional<User> findByEmail(String email){

        String encryptedEmail = encryptionService.encrypt(email);

        var user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no account with this email address."));

        return Optional.ofNullable(user);

    }

    public void deleteByEmail(String email){
        String encryptedEmail = encryptionService.encrypt(email);
        User user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no account with this email address."));
        user.getTokens().clear();
        tokenRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    public void addChild(String parentEmail, String childEmail){
//        jwtService.isValid();
        String encryptedParentEmail = encryptionService.encrypt(parentEmail);
        String encryptedChildEmail = encryptionService.encrypt(childEmail);

        User parent = userRepository.findByEmail(encryptedParentEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no parent account with this email address."));
        User child = userRepository.findByEmail(encryptedChildEmail)
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

        if(parent.getChildren().contains(child)){
            throw new UserAlreadyExistsException("The child is already added");
        }
        else {
            log.info("Adding new child {} to the parent {}.", childEmail, parentEmail);
            parent.getChildren().add(child);
            userRepository.save(parent);
        }
    }

    public List<User> getUsers(){
        log.info("Fetching all users.");
        List<User> list = userRepository.findAll();
        if(list.isEmpty()){
            throw new NoAreaFoundExceptions("There is no user defined!");
        }
        return list;
    }

    public  List<User> findAllChildrenByParentEmail(String email) throws RuntimeException{
        String encryptedEmail = encryptionService.encrypt(email);

        List<User> childrens = userRepository.findAllChildrenByEmail(encryptedEmail);

        if(childrens.isEmpty()){
            throw new NoChildFoundException("No child added yet!");
        }

        return childrens;
    }

    public User decryptUser(User user){
        User userDecrypted = User.builder()
                    .firstName(encryptionService.decrypt(user.getFirstName()))
                    .lastName(encryptionService.decrypt(user.getLastName()))
                    .email(encryptionService.decrypt(user.getEmail()))
                    .password(user.getPassword())
                    .phone(encryptionService.decrypt(user.getPhone()))
                    .age(user.getAge())
                    .role(user.getRole())
                    .type(user.getType())
                    .build();

        return userDecrypted;
    }

    public User encryptUser(User user){
        User userEncrypted = User.builder()
                .firstName(encryptionService.encrypt(user.getFirstName()))
                .lastName(encryptionService.encrypt(user.getLastName()))
                .email(encryptionService.encrypt(user.getEmail()))
                .password(user.getPassword())
                .phone(encryptionService.encrypt(user.getPhone()))
                .age(user.getAge())
                .role(user.getRole())
                .type(user.getType())
                .build();

        return userEncrypted;
    }
}