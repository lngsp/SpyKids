package ro.spykids.server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.exceptions.NoAreaFoundExceptions;
import ro.spykids.server.exceptions.NoChildFoundException;
import ro.spykids.server.exceptions.UserAlreadyExistsException;
import ro.spykids.server.model.User;
import ro.spykids.server.services.UserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/spykids/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    //GET

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable("id") Integer id) {
        try {
            User user = userService.decryptUser(userService.getUserById(id));

            return new ResponseEntity<>(user, new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getUserByEmail(@RequestParam("email") String email) {
        try {

            var user = userService.decryptUser(userService.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found")));

            return new ResponseEntity<>(user, new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUsers() {
        try{
           List<User> users = userService.getUsers();
            return new ResponseEntity<>(users, new HttpHeaders(), HttpStatus.OK);
        }catch (NoAreaFoundExceptions e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/children")
    public ResponseEntity<?> getAllChildrenByParentEmail(@RequestParam("parentEmail") String parentEmail) {
        try{
//            jwtService.checkHeaderToken(email, request);
            List<User> EncryptedChildrens = userService.findAllChildrenByParentEmail(parentEmail);

            List<User> DecryptedChildrens = new ArrayList<>();
            for(User u : EncryptedChildrens){
                DecryptedChildrens.add(userService.decryptUser(u));
            }

            return new ResponseEntity<>(DecryptedChildrens, new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoChildFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    //POST
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.save(user), new HttpHeaders(), HttpStatus.OK);
    }

    @PostMapping("/children")
    public ResponseEntity<?> addChild(@RequestParam("parentEmail") String parentEmail,
                                      @RequestParam("childEmail") String childEmail) {
        try {
//            jwtService.checkHeaderToken(parentEmail, request);
            userService.addChild(parentEmail, childEmail);

            return new ResponseEntity<>("Child successfully added!", new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
            //The child is already added
        }

    }

    //DELETE
    @DeleteMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        try {
            userService.deleteByEmail(email);
            return new ResponseEntity<>("User deleted.", new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

}