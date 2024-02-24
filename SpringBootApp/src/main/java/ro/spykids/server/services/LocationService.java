package ro.spykids.server.services;

import ro.spykids.server.controller.request.AddLocationRequest;
import ro.spykids.server.controller.response.IsInAreaResponse;
import ro.spykids.server.controller.response.LocationResponse;
import ro.spykids.server.exceptions.NoChildFoundException;
import ro.spykids.server.exceptions.NoLocationFoundException;
import ro.spykids.server.model.Location;
import ro.spykids.server.model.User;
import ro.spykids.server.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LocationService {
    private final LocationRepository locationRepository;
    private final AreaService areaService;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public void saveLocation(AddLocationRequest addLocationRequest) {
        String encryptedLatitude, encryptedLongitude;
        if(addLocationRequest.getLatitude() == null || addLocationRequest.getLongitude() == null){
            encryptedLatitude = encryptionService.encrypt("NaN");
            encryptedLongitude = encryptionService.encrypt("NaN");
        }
        else {
            encryptedLatitude = encryptionService.encrypt(addLocationRequest.getLatitude().toString());
            encryptedLongitude = encryptionService.encrypt(addLocationRequest.getLongitude().toString());
        }

        Optional<User> userExist = userService.findByEmail(addLocationRequest.getEmail());

        //check if user exist (child)
        if (userExist.isPresent()) {
            User user = userExist.get();

            Optional<Location> lastLocation = locationRepository.findTopByUserLOrderByCreatedAtDesc(user);
            //check is there is a last location for the user
            if(lastLocation.isPresent()) {

                //check if the last location geographical coordinates are the same with the one in the request
                if (lastLocation.get().getLatitude().equals(encryptedLatitude) &&
                        lastLocation.get().getLongitude().equals(encryptedLongitude)) {

                    //same coordinates -> update departured time
                    Location locationToUpdate = lastLocation.get();
                    locationToUpdate.setDepartureTime(LocalDateTime.now());
                    locationRepository.save(locationToUpdate);
                }
                else {
                    //different coordinates -> insert new location
                    Location newLocation = Location.builder()
                            .latitude(encryptedLatitude)
                            .longitude(encryptedLongitude)
                            .userL(user)
                            .createdAt(LocalDateTime.now())
                            .arrivalTime(LocalDateTime.now())
                            .departureTime(LocalDateTime.now())
                            .build();
                    locationRepository.save(newLocation);
                    System.out.println("New location added: " + newLocation);
                }
            }
            else {
                //if there is no last location -> add new location
                Location newLocation = Location.builder()
                        .latitude(encryptedLatitude)
                        .longitude(encryptedLongitude)
                        .userL(user)
                        .createdAt(LocalDateTime.now())
                        .arrivalTime(LocalDateTime.now())
                        .departureTime(LocalDateTime.now())
                        .build();
                locationRepository.save(newLocation);
                System.out.println("New location added: " + newLocation);
            }
        }
        else {
            // User doesnt exist
            throw new UsernameNotFoundException("The email is not asosiated with an account!");
        }
    }

    public List<LocationResponse> getAllChildLocations(String email) throws RuntimeException{

        List<LocationResponse> responseList = new ArrayList<>();

        try {
            Optional<User> ur = userService.findByEmail(email);
            if(ur.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }
            List<User> allChildren = userService.findAllChildrenByParentEmail(email);// find the child of the parent using parentEmail

            for (User child:allChildren) {
                String decryptedChildEmail = encryptionService.decrypt(child.getEmail());

                List<Location> childLocations = locationRepository.findAllByUserLId(child.getId());

                if(childLocations.isEmpty()){
                    throw new NoLocationFoundException("No location found!");
                }

                for (Location loc:childLocations) {

                    Double decryptedLatitude = Double.valueOf(encryptionService.decrypt(loc.getLatitude()));
                    Double decryptedLongitude = Double.valueOf(encryptionService.decrypt(loc.getLongitude()));

                    List<IsInAreaResponse> isInAreaResponseList = areaService.checkLocationAllArea(
                            decryptedChildEmail,
                            decryptedLatitude,
                            decryptedLongitude);

                    String message = checkAreaSafeZone(child, isInAreaResponseList);

                    LocationResponse response = new LocationResponse();
                    response.setChildEmail(decryptedChildEmail); //show to user the email of the child decrypted
                    response.setLatitude(decryptedLatitude);
                    response.setLongitude(decryptedLongitude);
                    response.setArrivalTime(loc.getArrivalTime());
                    response.setDepartureTime(loc.getDepartureTime());
                    response.setMessages(message);
                    responseList.add(response);
                }
            }
            return responseList;

        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        } catch (NoLocationFoundException e) {
            throw new NoLocationFoundException(e.getMessage());
        }catch (NoChildFoundException e){
            throw new NoChildFoundException(e.getMessage());
        }
    }


    public  List<LocationResponse> getChildLastLocations(String email) throws NoChildFoundException, NoLocationFoundException {
        // this method is made for MANY kids - return a list of the last location for every child

        List<LocationResponse> responseList = new ArrayList<>();

        try{
            Optional<User> userExist = userService.findByEmail(email);
            if(userExist.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }

            List<User> allChildren = userService.findAllChildrenByParentEmail(email);        // find the child of the parent using parentEmail
            for (User child:allChildren) {
                String decryptedChildEmail = encryptionService.decrypt(child.getEmail());

                Location lastLocation = locationRepository.findTopByUserLEmailOrderByDepartureTimeDesc(child.getEmail());

                if(lastLocation == null){
                    throw new NoLocationFoundException("No location found!");
                }

                Double decryptedLatitude = Double.valueOf(encryptionService.decrypt(lastLocation.getLatitude()));
                Double decryptedLongitude = Double.valueOf(encryptionService.decrypt(lastLocation.getLongitude()));

                List<IsInAreaResponse> isInAreaResponseList = areaService.checkLocationAllArea(
                        decryptedChildEmail,
                        decryptedLatitude,
                        decryptedLongitude);

                String message = checkAreaSafeZone(child, isInAreaResponseList);

                // create the Location Response with lat, long, arrival time and departured time
                LocationResponse response = new LocationResponse();
                response.setChildEmail(decryptedChildEmail);
                response.setLatitude(decryptedLatitude);
                response.setLongitude(decryptedLongitude);
                response.setArrivalTime(lastLocation.getArrivalTime());
                response.setDepartureTime(lastLocation.getDepartureTime());
                response.setMessages(message);
                responseList.add(response);
            }
            return responseList;

        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        } catch (NoLocationFoundException e) {
            throw new NoLocationFoundException(e.getMessage());
        }catch (NoChildFoundException e){
            throw new NoChildFoundException(e.getMessage());
        }
    }

    public List<LocationResponse> getChildAllLocationsFromDate(String email, Date date) throws RuntimeException{
        // get all location for every child from a date

        List<LocationResponse> responseList = new ArrayList<>();

        try {
            Optional<User> ur = userService.findByEmail(email);

            if(ur.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }

            List<User> allChildren = userService.findAllChildrenByParentEmail(email);        // find the child of the parent using parentEmail

            for (User child:allChildren) {
                String decryptedChildEmail = encryptionService.decrypt(child.getEmail());

                List<Location> childLocations = locationRepository.findByUserIdAndArrivalDate(child.getId(), date);

                if(childLocations.isEmpty()){
                    throw new NoLocationFoundException("No location found!");
                }

                for (Location loc:childLocations) {

                    Double decryptedLatitude = Double.valueOf(encryptionService.decrypt(loc.getLatitude()));
                    Double decryptedLongitude = Double.valueOf(encryptionService.decrypt(loc.getLongitude()));

                    List<IsInAreaResponse> isInAreaResponseList = areaService.checkLocationAllArea(
                            decryptedChildEmail,
                            decryptedLatitude,
                            decryptedLongitude);

                    String message = checkAreaSafeZone(child, isInAreaResponseList);

                    LocationResponse response = new LocationResponse();
                    response.setChildEmail(decryptedChildEmail);
                    response.setLatitude(decryptedLatitude);
                    response.setLongitude(decryptedLongitude);
                    response.setArrivalTime(loc.getArrivalTime());
                    response.setDepartureTime(loc.getDepartureTime());
                    response.setMessages(message);
                    responseList.add(response);
                }
            }
            return responseList;

        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        } catch (NoLocationFoundException e) {
            throw new NoLocationFoundException(e.getMessage());
        }catch (NoChildFoundException e){
            throw new NoChildFoundException(e.getMessage());
        }
    }

    public List<LocationResponse> getChildAllLocationsSinceDate(String email, Date date) throws RuntimeException{
        // get all location for every child, where the arrival_date is between the date (from request) and now (CURRENT_TIMESTAMP)
        // used when user want to get the location from last week / month / year

        List<LocationResponse> responseList = new ArrayList<>();

        try {
            Optional<User> ur = userService.findByEmail(email);
            System.out.println("USERUL ESTE " + ur.get().getEmail().toString());

            if(ur.get().getType().toString().equals("CHILD")){
                throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
            }

            List<User> allChildren = userService.findAllChildrenByParentEmail(email);  // find the child of the parent using parentEmail

            for (User child:allChildren) {
                String decryptedChildEmail = encryptionService.decrypt(child.getEmail());

                System.out.println("USER ID : " + child.getId().toString() + " " + decryptedChildEmail);

                List<Location> childLocations = locationRepository.findByUserIdAndArrivalDateBetween(child.getId(), date);

                System.out.println("CHILD LOCATIONS " + childLocations.get(0).toString());

                if(childLocations.isEmpty()){
                    throw new NoLocationFoundException("No location found!");
                }

                for (Location loc:childLocations) {

                    Double decryptedLatitude = Double.valueOf(encryptionService.decrypt(loc.getLatitude()));
                    Double decryptedLongitude = Double.valueOf(encryptionService.decrypt(loc.getLongitude()));

                    List<IsInAreaResponse> isInAreaResponseList = areaService.checkLocationAllArea(
                            decryptedChildEmail,
                            decryptedLatitude,
                            decryptedLongitude);

                    String message = checkAreaSafeZone(child, isInAreaResponseList);

                    LocationResponse response = new LocationResponse();
                    response.setChildEmail(decryptedChildEmail);
                    response.setLatitude(decryptedLatitude);
                    response.setLongitude(decryptedLongitude);
                    response.setArrivalTime(loc.getArrivalTime());
                    response.setDepartureTime(loc.getDepartureTime());
                    response.setMessages(message);
                    responseList.add(response);
                }
            }
            return responseList;

        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException(e.getMessage());
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        } catch (NoLocationFoundException e) {
            throw new NoLocationFoundException(e.getMessage());
        }catch (NoChildFoundException e){
            throw new NoChildFoundException(e.getMessage());
        }
    }

    private String checkAreaSafeZone(User u, List<IsInAreaResponse> isInAreaResponseList){
        String decryptedEmail = encryptionService.decrypt(u.getEmail());

        String message ="";
        boolean isInAnUndefinedArea = true;
        String name = "";

        for (IsInAreaResponse area:isInAreaResponseList) {
            if(area.getIsInArea() == true) {
                name = area.getAreaName();
                if(area.getSafe() == true){
                    message = "Child " + decryptedEmail + " in safe area:" + name +"!";
                    isInAnUndefinedArea = false;
                    break;
                } else {
                    message = "Child " + decryptedEmail + " in restricted area:" + name +"!";
                    isInAnUndefinedArea = false;
                    break;
                }
            }
        }

        if(isInAnUndefinedArea == true){
            message = "Child " + decryptedEmail + " is in an undefined area!";
        }

        return message;
    }

}