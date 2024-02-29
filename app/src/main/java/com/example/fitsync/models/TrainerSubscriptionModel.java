package com.example.fitsync.models;

public class TrainerSubscriptionModel {
    TrainerModel trainerModel;
    String modeOfPayment,membershipPlan,dateOfJoining,dateOfEnding;
    Double payment;
    Boolean paymentStatus;

    public TrainerSubscriptionModel() {
    }

    public TrainerSubscriptionModel(TrainerModel trainerModel,String membershipPlan,String dateOfJoining,
                                    String dateOfEnding,String modeOfPayment,Double payment, Boolean paymentStatus) {
        this.trainerModel = trainerModel;
        this.membershipPlan = membershipPlan;
        this.dateOfJoining = dateOfJoining;
        this.dateOfEnding = dateOfEnding;
        this.modeOfPayment = modeOfPayment;
        this.payment = payment;
        this.paymentStatus = paymentStatus;
    }

    public TrainerModel getTrainerModel() {
        return trainerModel;
    }

    public void setTrainerModel(TrainerModel trainerModel) {
        this.trainerModel = trainerModel;
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

    public Double getPayment() {
        return payment;
    }

    public void setPayment(Double payment) {
        this.payment = payment;
    }

    public String getMembershipPlan() {
        return membershipPlan;
    }

    public void setMembershipPlan(String membershipPlan) {
        this.membershipPlan = membershipPlan;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getDateOfEnding() {
        return dateOfEnding;
    }

    public void setDateOfEnding(String dateOfEnding) {
        this.dateOfEnding = dateOfEnding;
    }
}
