package ro.spykids.server.controller;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ro.spykids.server.controller.request.AddChildToRestrictedAreaRequest;
import ro.spykids.server.controller.response.AreaResponse;
import ro.spykids.server.exceptions.*;
import ro.spykids.server.model.Area;
import ro.spykids.server.services.AreaService;

import java.util.List;

@RestController
@RequestMapping("/api/spykids/areas")
@RequiredArgsConstructor
public class AreaController {
    private final AreaService areaService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAreas() {
        try{
            List<Area> areas = areaService.findAll();
            return new ResponseEntity<>(areas, new HttpHeaders(), HttpStatus.OK);
        }catch (NoAreaFoundExceptions e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/children")
    public ResponseEntity<?> getChildArea(@RequestParam("childEmail") String childEmail) {
        try{
            List<AreaResponse> list = areaService.getChildArea(childEmail);
            return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (NoAreaFoundExceptions e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/parent")
    public ResponseEntity<?> getParentArea(@RequestParam("parentEmail") String parentEmail) {
        try{
            List<AreaResponse> list = areaService.getParentAreas(parentEmail);
            return new ResponseEntity<>(list, new HttpHeaders(), HttpStatus.OK);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (NoAreaFoundExceptions e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> defineArea(@RequestBody Area area){
        try{
            areaService.saveArea(area);
            return new ResponseEntity<>("Area successfully added!", new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (AreaAlreadyExistsException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (UserNotAllowedException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (TooManyPointsException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.REQUEST_ENTITY_TOO_LARGE);
        }
    }

    @PostMapping("/children")
    public ResponseEntity<?> addChild(@RequestBody AddChildToRestrictedAreaRequest request){
        try{
            areaService.addChild(request);
            return new ResponseEntity<>("Child successfully added!", new HttpHeaders(), HttpStatus.OK);
        }catch (UsernameNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (DifferentParentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (AreaAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
        }catch (NoAreaFoundExceptions e){
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/safe")
    public ResponseEntity<?> modifySafety(@RequestParam("definedBy") String definedBy,
                                          @RequestParam("name") String name,
                                          @RequestParam("safe") Boolean safe){
        try{
            areaService.modifySafety(definedBy, name, safe);
            return new ResponseEntity<>("Area safety has been updated successfully!", new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (NoAreaFoundExceptions e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/enable")
    public ResponseEntity<?> modifyEnable(@RequestParam("definedBy") String definedBy,
                                          @RequestParam("name") String name,
                                          @RequestParam("enable") Boolean enable){
        try{
            areaService.modifyEnable(definedBy, name, enable);
            String msg = "";
            if(enable==true)
                msg = "enabled";
            else msg = "disabled";
            return new ResponseEntity<>("Area has been updated successfully, is now " + msg + "!", new HttpHeaders(), HttpStatus.OK);
        } catch (InvalidJwtAuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
        }catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
        }catch (NoAreaFoundExceptions e) {
            return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

}

