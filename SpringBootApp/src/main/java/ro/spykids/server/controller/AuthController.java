package ro.spykids.server.controller;

import org.springframework.web.bind.annotation.*;
import ro.spykids.server.controller.request.AuthRequest;
import ro.spykids.server.controller.request.RegisterRequest;
import ro.spykids.server.controller.response.AuthResponse;
import ro.spykids.server.exceptions.IncompleteRequestException;
import ro.spykids.server.exceptions.IncorectAgeException;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.exceptions.UserAlreadyExistsException;
import ro.spykids.server.services.AuthService;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/spykids/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")   //sign in - inregistrare
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){

        try{
            AuthResponse response = authService.register(request);
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        }
        catch (UserAlreadyExistsException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
        catch (IncompleteRequestException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        catch (ValidationException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        catch (IncorectAgeException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/authenticate")   //login
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest request){
        try {
            AuthResponse response = authService.authenticate(request);
            return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        catch (IncompleteRequestException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (LockedException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.LOCKED);
        } catch (DisabledException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        } catch (CredentialsExpiredException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam("email") String email){
        try{
            authService.logout(email);
            return new ResponseEntity<>("Logout successfully", new HttpHeaders(), HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
        catch (IncompleteRequestException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }catch (InvalidJwtAuthenticationException e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (LockedException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.LOCKED);
        } catch (DisabledException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        } catch (CredentialsExpiredException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
