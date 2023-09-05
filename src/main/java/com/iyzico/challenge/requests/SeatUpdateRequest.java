package com.iyzico.challenge.requests;

public class SeatUpdateRequest {
    private String newSeatNumber;

    public String getNewSeatNumber() {
        return newSeatNumber;
    }

    public void setNewSeatNumber(String newSeatNumber) {
        this.newSeatNumber = newSeatNumber;
    }

    public boolean isNewBookedStatus() {
        return newBookedStatus;
    }

    public void setNewBookedStatus(boolean newBookedStatus) {
        this.newBookedStatus = newBookedStatus;
    }

    private boolean newBookedStatus;
}
