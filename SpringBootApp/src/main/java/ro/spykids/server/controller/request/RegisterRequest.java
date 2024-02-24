package ro.spykids.server.controller.request;

import ro.spykids.server.controller.AuthController;
import ro.spykids.server.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest  {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Integer age;

    private Role role;
    private String type;

}
