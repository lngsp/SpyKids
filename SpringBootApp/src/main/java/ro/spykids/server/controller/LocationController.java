package ro.spykids.server.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import ro.spykids.server.jwt.JwtService;
import ro.spykids.server.controller.request.AddLocationRequest;
import ro.spykids.server.controller.response.LocationResponse;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.exceptions.LocationAlreadyExists;
import ro.spykids.server.exceptions.NoChildFoundException;
import ro.spykids.server.exceptions.NoLocationFoundException;
import ro.spykids.server.services.LocationService;
import ro.spykids.server.services.AreaService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/api/spykids/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/")
    public ResponseEntity<?> getLocations(@RequestParam("parentEmail") String parentEmail,
                                          HttpServletRequest request) {

        try {
            List<LocationResponse> locations = locationService.getAllChildLocations(parentEmail);

            return new ResponseEntity<>(locations, new HttpHeaders(), HttpStatus.OK);
        }catch (ExpiredJwtException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }
        catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (TokenExpiredException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        } catch (NoChildFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoLocationFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/date/")
    public  ResponseEntity<?> getLocationsFromDate(@RequestParam("parentEmail") String parentEmail,
                                                   @RequestParam("date") Date date) {
        try{
            //jwtService.checkHeaderToken(locationDateRequest.getEmail(), request);
            List<LocationResponse> locations = locationService.getChildAllLocationsFromDate(parentEmail, date);
            return new ResponseEntity<>(locations, new HttpHeaders(), HttpStatus.OK);

        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoChildFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoLocationFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/since/")
    public  ResponseEntity<?> getLocationsSinceDate(@RequestParam("parentEmail") String parentEmail,
                                                    @RequestParam("date") Date date) {
        try{

            List<LocationResponse> locations =
                    locationService.getChildAllLocationsSinceDate(parentEmail, date);

            return new ResponseEntity<>(locations, new HttpHeaders(), HttpStatus.OK);

        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoChildFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoLocationFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/last/")
    public ResponseEntity<?> getLastLocation(@RequestParam("parentEmail") String parentEmail){
        try{
//            jwtService.checkHeaderToken(parentEmail, request);
            List<LocationResponse> locationResponse = locationService.getChildLastLocations(parentEmail);

            return new ResponseEntity<>(locationResponse, new HttpHeaders(), HttpStatus.OK);

        } catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (NoChildFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        } catch (NoLocationFoundException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/")
    public ResponseEntity<?> addLocation(@RequestBody AddLocationRequest addLocationRequest){
        try {
//            jwtService.checkHeaderToken(addLocationRequest.getEmail(), request);

            locationService.saveLocation(addLocationRequest);
            return new ResponseEntity<>("Location successfully added!", new HttpHeaders(), HttpStatus.OK);

        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (LocationAlreadyExists e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (NoLocationFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

}
