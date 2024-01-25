package com.example.fitsync.models;

public class MemberModel {
    String firstName,lastName,gender,memberUsername,memberPassword,
            dateOfJoining,dateOfEnding,membershipPlan,modeOfPayment;
    Boolean paymentStatus;
    TrainerSubscriptionModel trainerSubscriptionPlan;

    public MemberModel() {
    }

    public MemberModel(String firstName, String lastName, String gender, String memberUsername,
                       String memberPassword, String dateOfJoining, String dateOfEnding, String membershipPlan
    ,String modeOfPayment,Boolean paymentStatus,TrainerSubscriptionModel trainerSubscriptionModel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.memberUsername = memberUsername;
        this.memberPassword = memberPassword;
        this.dateOfJoining = dateOfJoining;
        this.dateOfEnding = dateOfEnding;
        this.membershipPlan = membershipPlan;
        this.modeOfPayment = modeOfPayment;
        this.paymentStatus = paymentStatus;
        this.trainerSubscriptionPlan = trainerSubscriptionModel;
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

    public String getMemberUsername() {
        return memberUsername;
    }

    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    public String getMemberPassword() {
        return memberPassword;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public String getDateOfEnding() {
        return dateOfEnding;
    }

    public void setDateOfEnding(String dateOfEnding) {
        this.dateOfEnding = dateOfEnding;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public TrainerSubscriptionModel getTrainerSubscriptionModel() {
        return trainerSubscriptionPlan;
    }

    public void setTrainerSubscriptionModel(TrainerSubscriptionModel trainerSubscriptionModel) {
        this.trainerSubscriptionPlan = trainerSubscriptionModel;
    }
}
