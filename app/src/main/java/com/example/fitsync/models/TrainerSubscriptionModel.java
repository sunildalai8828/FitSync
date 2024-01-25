package com.example.fitsync.models;

public class TrainerSubscriptionModel {
    TrainerModel trainerModel;
    String modeOfPayment,membershipPlan;
    Double payment;
    Boolean paymentStatus;

    public TrainerSubscriptionModel() {
    }

    public TrainerSubscriptionModel(TrainerModel trainerModel,String membershipPlan ,
                                    String modeOfPayment,Double payment, Boolean paymentStatus) {
        this.trainerModel = trainerModel;
        this.membershipPlan = membershipPlan;
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
}
