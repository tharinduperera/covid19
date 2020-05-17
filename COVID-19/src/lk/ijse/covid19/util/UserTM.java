package lk.ijse.covid19.util;

import javafx.scene.control.Button;

public class UserTM {

    private String username;
    private String name;
    private String role;
    private Button removebtn;

    public UserTM() {
    }

    public UserTM(String username, String name, String role, Button removebtn) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.removebtn = removebtn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Button getRemovebtn() {
        return removebtn;
    }

    public void setRemovebtn(Button removebtn) {
        this.removebtn = removebtn;
    }

    @Override
    public String toString() {
        return "UserTM{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", removebtn=" + removebtn +
                '}';
    }
}
