package ro.spykids.clientapp.pojo;


import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private java.lang.String firstName, lastName;

    private java.lang.String email;

    private java.lang.String password;

    private java.lang.String phone;

    private Integer age;

    private java.lang.String type;

    private String token;


    public User(){}

    public User(String firstName, String lastName, String email, String password, String phone, Integer age,  String type, String token) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.type = type;
        this.token = token;
    }

    public User(String firstName, String lastName, String email, String password, String phone, Integer age, String type, String token, List<String> childsEmail) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.type = type;
        this.token = token;
    }


    public java.lang.String getFirstName() {
        return firstName;
    }

    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }

    public java.lang.String getLastName() {
        return lastName;
    }

    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }

    public java.lang.String getEmail() {
        return email;
    }

    public void setEmail(java.lang.String email) {
        this.email = email;
    }

    public java.lang.String getPassword() {
        return password;
    }

    public void setPassword(java.lang.String password) {
        this.password = password;
    }

    public java.lang.String getPhone() {
        return phone;
    }

    public void setPhone(java.lang.String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public java.lang.String getType() {
        return type;
    }

    public void setType(java.lang.String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
