package com.example.fitsync.models;

public class TrainerModel {
    String firstName,lastName,gender,trainerUsername,trainerPassword,dateOfJoining,experience;

    public TrainerModel() {
    }

    public TrainerModel(String firstName, String lastName, String gender, String trainerUsername,
                        String trainerPassword, String dateOfJoining, String experience) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.trainerUsername = trainerUsername;
        this.trainerPassword = trainerPassword;
        this.dateOfJoining = dateOfJoining;
        this.experience = experience;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getTrainerPassword() {
        return trainerPassword;
    }

    public void setTrainerPassword(String trainerPassword) {
        this.trainerPassword = trainerPassword;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }
}
