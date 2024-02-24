package ro.spykids.server.controller;

import ro.spykids.server.controller.request.AddBatteryRequest;
import ro.spykids.server.controller.response.BatteryResponse;
import ro.spykids.server.exceptions.InvalidJwtAuthenticationException;
import ro.spykids.server.exceptions.NoChildFoundException;
import ro.spykids.server.services.BatteryService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spykids/batteries")
@RequiredArgsConstructor
public class BatteryController {

    private final BatteryService batteryService;

    @GetMapping("/")
    public ResponseEntity<?> getLastBattery(@RequestParam("parentEmail") String parentEmail) {
        try{
            List<BatteryResponse> battery =  batteryService.getLastBattery(parentEmail);
            return new ResponseEntity<>(battery, new HttpHeaders(), HttpStatus.OK);
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

    @PostMapping("/")
    public ResponseEntity<?> saveBattery(@RequestBody AddBatteryRequest request) {
        try{
            batteryService.save(request);
            return new ResponseEntity<>("Battery successfully added!", new HttpHeaders(), HttpStatus.OK);
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
}
