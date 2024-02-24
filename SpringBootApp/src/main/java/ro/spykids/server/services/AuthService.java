package ro.spykids.server.services;
// The following code was written by Bouali Ali and adapted and modity for use in this application.

import ro.spykids.server.jwt.JwtService;
import ro.spykids.server.controller.request.AuthRequest;
import ro.spykids.server.controller.request.RegisterRequest;
import ro.spykids.server.controller.response.AuthResponse;
import ro.spykids.server.exceptions.IncompleteRequestException;
import ro.spykids.server.exceptions.IncorectAgeException;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.exceptions.UserAlreadyExistsException;
import ro.spykids.server.model.Role;
import ro.spykids.server.model.Token;
import ro.spykids.server.model.User;
import ro.spykids.server.repository.TokenRepository;
import ro.spykids.server.repository.UserRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;
    private final EncryptionService encryptionService;


    public AuthResponse register(RegisterRequest request) {
        String encryptedEmail = encryptionService.encrypt(request.getEmail());
        String encryptedFirstName = encryptionService.encrypt(request.getFirstName());
        String encryptedLastName = encryptionService.encrypt(request.getLastName());
        String encryptedPhone = encryptionService.encrypt(request.getPhone());

        //validate params
        try {
            validateFirstName(request.getFirstName());
            validateLastName(request.getLastName());
            validateEmail(request.getEmail());
            validatePassword(request.getPassword());
            validateAge(request.getAge());
            validatePhone(request.getPhone());
        } catch (ValidationException e) {
            throw new ValidationException(e.getMessage());
        }

        Optional<User> existingUser = userRepository.findByEmail(encryptedEmail);

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("The email is associated with an account!");
        }

        Optional<User> existingPhone = userRepository.findByPhone(encryptedPhone);

        if (existingPhone.isPresent()) {
            throw new UserAlreadyExistsException("The phone is associated with an account!");
        }

        //check if the fields are empty
        if (request.getFirstName() == null || request.getLastName() == null || request.getEmail() == null ||
                request.getPhone() == null || request.getAge() == null || request.getPassword() == null || request.getType() == null) {
            throw new IncompleteRequestException("Please fill in all required fields!");
        }

        if(request.getType().equals("CHILD") && request.getAge()>18){
            throw new IncorectAgeException("The age for a child must be less than 18 years");
        }

        if(request.getType().equals("PARENT") && request.getAge()<18){
            throw new IncorectAgeException("The age for a parent must be greater than 18 years");
        }


        var user = User.builder()
                .firstName(encryptedFirstName)
                .lastName(encryptedLastName)
                .email(encryptedEmail)
                .phone(encryptedPhone)
                .age(request.getAge())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .type(request.getType())
                .build();

        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return AuthResponse.builder().token(jwtToken).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        //check if the fields are empty
        if (request.getEmail() == null ||request.getPassword() == null) {
            throw new IncompleteRequestException("Please fill in all required fields!");
        }

        String encryptedEmail = encryptionService.encrypt(request.getEmail());

        var user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            encryptedEmail,
                            request.getPassword()
                    )
            );
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("Please fill in all required fields!");
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        } catch (LockedException e) {
            throw new BadCredentialsException("User account is locked");
        } catch (DisabledException e) {
            throw new BadCredentialsException("User account is disabled");
        } catch (CredentialsExpiredException e) {
            throw new BadCredentialsException("User credentials have expired");
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Authentication failed");
        } catch (Exception e) {
            throw new BadCredentialsException("An unexpected error occurred");
        }


        var jwtToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthResponse.builder().token(jwtToken).build();
    }

    public void logout(String email){
        //check if the fields are empty
        if (email == null) {
            throw new IncompleteRequestException("Please fill in required fields!");
        }

        String encryptedEmail = encryptionService.encrypt(email);

        var user = userRepository.findByEmail(encryptedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try{
            revokeAllUserTokens(user);
        }catch (InvalidJwtAuthenticationException e){
            throw new InvalidJwtAuthenticationException(e.getMessage());
        }


    }

    private void saveUserToken(User user, String jwtToken) {

        var token = Token.builder()
                .userT(user)
                .value(jwtToken)
                .type("BEARER")
                .expired(false)
                .revoked(false)
                .build();

        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        try{
            var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
            if (validUserTokens.isEmpty())
                return;
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validUserTokens);
        }catch (InvalidJwtAuthenticationException e){
            throw new InvalidJwtAuthenticationException(e.getMessage());
        }

    }

    //VALIDATE PARAMETERS
    private boolean validateFirstName(String firstName){
        if(containDigit(firstName)==true){
            throw new ValidationException("First name should only consist of letters!");
        }
        else if(firstName.length()<3){
            throw new ValidationException("First name must contain at least 3 characters!");
        }
        else if(firstName.length()>50){
            throw new ValidationException("First name must contain at most 50 characters!");
        }

        return true;

    }

    private boolean containDigit(String inputString){  //verify if the name contain digits
        char[] chars = inputString.toCharArray();
        for(char character : chars){
            if(Character.isDigit(character)){
                return true;
            }
        }
        return false;
    }

    private boolean validateLastName(String lastName){
        if(containDigit(lastName)==true){
            throw new ValidationException("Last name should only consist of letters!");
        }
        else if(lastName.length()<3){
            throw new ValidationException("Last name must contain at least 3 characters!");
        }
        else if(lastName.length()>50){
            throw new ValidationException("Last name must contain at most 50 characters!");
        }

        return true;

    }

    private boolean validateEmail(String email){
        String checkEmail  = "[a-zA-Z0-9._-]+@(gmail|yahoo|hotmail|outlook|icloud|aol|zoho|gmx)\\.(com|net|org|edu|info|biz|me|co|gov)";
        if(email.length()<5){
            throw new ValidationException("Email must contain at least 5 characters!");
        }
        else if(email.length()>50){
            throw new ValidationException("Email must contain at most 50 characters!");
        }
        else if(!email.matches(checkEmail )){
            throw new ValidationException("Please enter a valid email!");
        }
        return true;
    }

    private boolean validatePassword(String password){
        String checkPassword = "^" +
                "(?=.*[0-9])" +         // cel puțin 1 cifră
                "(?=.*[a-z])" +         // cel puțin 1 literă mică
                "(?=.*[A-Z])" +         // cel puțin 1 literă mare
                "(?=.*[a-zA-Z])" +      // orice literă
                "(?=.*[@#$%^.&+=])" +   // cel puțin 1 caracter special
                "(?=\\S+$)" +           // fără spații albe
                ".{8,}" +               // cel puțin 5 caractere
                "$";

        if(password.length()>50){
            throw new ValidationException("Password must contain at most 50 characters!");
        }
        else if(!password.matches(checkPassword)){
            throw new ValidationException("Invalid password. It must contain: " +
                    "\n - at least 8 characters long, " +
                    "\n - at least one digit, " +
                    "\n - at least one upper letter, " +
                    "\n - at least one lower letter, " +
                    "\n - at least one special character, " +
                    "\n - not contain any whitespace)!");
        }

        return true;
    }

    private boolean validateAge(Integer age){
        String a = age.toString();
        if(a.length()<1){
            throw new ValidationException("Age must contain at least 1 characters!");
        }
        else if(a.length()>3){
            throw new ValidationException("Age must contain at most 3 characters!");
        }
        return true;
    }

    private boolean validatePhone(String phone){

        if(phone.length()<10){
            throw new ValidationException("Phone must contain at least 10 characters!");
        }
        else if(phone.length()>13){
            throw new ValidationException("Phone must contain at most 13 characters!");
        } else if (containsOnlyDigits(phone) == false) {
            throw new ValidationException("Phone must contain only digits!");
        } else if(phone.startsWith("0")==false){
            throw new ValidationException("Phone must start with digit 0!");
        }
        return true;
    }

    private boolean containsOnlyDigits(String inputString) {
        return inputString.matches("[0-9]+");
    }

    //END OF VALIDATE PARAMETERS
}
