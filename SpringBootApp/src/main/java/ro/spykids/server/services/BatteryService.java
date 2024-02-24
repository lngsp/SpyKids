package ro.spykids.server.services;

import ro.spykids.server.controller.request.AddBatteryRequest;
import ro.spykids.server.controller.response.BatteryResponse;
import ro.spykids.server.exceptions.NoBatteryFoundException;
import ro.spykids.server.model.Battery;
import ro.spykids.server.model.User;
import ro.spykids.server.repository.BatteryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BatteryService {
    private final BatteryRepository batteryRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;

    public void save(AddBatteryRequest request){
        User child = userService.findByEmail(request.getChildEmail())
                .orElseThrow(() -> new UsernameNotFoundException("There is no child account with this email address."));

       Battery battery = batteryRepository.findTopByUserBOrderByIdDesc(child);
       //if the percent is the same, update the time
       if(battery!=null && battery.getPercent()==request.getPercent()){
           battery.setTime(LocalDateTime.now());
       }
       else {
           Battery batteryToSave = new Battery();
           batteryToSave.setTime(LocalDateTime.now());
           batteryToSave.setPercent(request.getPercent());
           batteryToSave.setMessage(checkPercent(request.getPercent()));
           batteryToSave.setUserB(child);

           batteryRepository.save(batteryToSave);
       }


    }

    public List<BatteryResponse> getLastBattery(String parentEmail){

        Optional<User> ur = userService.findByEmail(parentEmail);
        if(ur.get().getType().toString().equals("CHILD")){
            throw new IllegalArgumentException("Invalid user type (CHILD). Cannot perform this operation.");
        }

        List<User> allChildrenByEmail = userService.findAllChildrenByParentEmail(parentEmail);   // find the child of the parent using parentEmail

        List<BatteryResponse> list = new ArrayList<>();

        for (User u:allChildrenByEmail) {
            Battery battery = batteryRepository.findTopByUserBOrderByIdDesc(u);
            if(battery == null){
                throw new NoBatteryFoundException("No battery found!");
            }
            BatteryResponse batteryResponse = new BatteryResponse(
                    battery.getPercent(),
                    battery.getMessage(),
                    battery.getTime(),
                    encryptionService.decrypt(battery.getUserB().getEmail()));

            list.add(batteryResponse);
        }
        return list;
    }

    private String checkPercent(Integer percent){
        String message ="";
        if(percent>50)
        {
            message = "The battery percentage is very good!";
        } else if (percent<=50 && percent>30) {
            message = "The battery percentage is low!";
        }
        else if(percent<=30 && percent>10){
            message = "The battery is almost discharged!";
        }
        else if(percent<=10){
            message = "The battery percentage is low. The phone will close soon!";
        }
        return message;
    }
}
