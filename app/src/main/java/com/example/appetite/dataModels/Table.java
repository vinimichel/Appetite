package com.example.appetite.dataModels;

import java.util.ArrayList;

public class Table {

    public static int freeSeats = 0;
    public static int tableCount = 0;
    private int id;
    private int seatCount;
    private boolean occupied;

    public Table(int seatCount) {
        tableCount ++;
        freeSeats += seatCount;
        this.id = tableCount;
        this.seatCount = seatCount;
        this.occupied = false;
    }

    public boolean getStatus() {
        return this.occupied;
    }

    public int getID() {
        return this.id;
    }

    public int getSeatCount() {
        return this.seatCount;
    }

    public void changeOccupiedStatus() {
        if (this.occupied) {
            this.occupied = false;
            freeSeats += this.seatCount;
        } else {
            this.occupied = true;
            freeSeats -= this.seatCount;
        }
    }

    public void changeSeatCount(int newSeatCount) {
        this.seatCount = newSeatCount;
    }

    public void deleteTable (){
        tableCount--;
        if (!this.getStatus()) {
            freeSeats -= this.seatCount;
        }
    }

    public int getFreeSeats() {
        return this.freeSeats;
    }
}
