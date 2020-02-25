package com.example.assignment4_inspirationrewards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfile implements Serializable {
    private String studentId = "A20435695";
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private int pointsToAward;
    private String department;
    private String story;
    private String position;
    private boolean admin;
    private String location;
    private String photo;
    private int pointsGet;
    List<Rewards>rewardRecords = new ArrayList<>();

    public int getPointsGet() {
        return pointsGet;
    }

    public void setPointsGet(int pointsGet) {
        this.pointsGet = pointsGet;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPointsToAward() {
        return pointsToAward;
    }

    public void setPointsToAward(int pointsToAward) {
        this.pointsToAward = pointsToAward;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Rewards> getRewardRecords() {
        return rewardRecords;
    }

    public void setRewardRecords(List<Rewards> rewardRecords) {
        this.rewardRecords = rewardRecords;
    }
}
