package com.example.admin.task3;

/**
 * Created by admin on 07-07-2018.
 */

public class users {
    public String name;
    public String password;
    public String status;
    public String email;

    public users(){

    }
    public users(String name, String password, String status,String email) {
        this.name = name;
        this.password = password;
        this.status = status;
        this.email=email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(){
        this.password=password;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email ){
        this.email=email;
    }
}
