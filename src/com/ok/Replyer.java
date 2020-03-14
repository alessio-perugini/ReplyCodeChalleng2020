package com.ok;

public abstract class Replyer {
    int bonus;
    String company;
    boolean chosen=false;
    int[] coordinateXY;

    public boolean isChosen(){
        return chosen;
    }

    public float getBonus(){
        return bonus;
    }

    public String getCompany() {
        return company;
    }

    public int[] getCoordinateXY() {
        return coordinateXY;
    }

    public void setCoordinateXY(int[] coordinateXY) {
        this.coordinateXY = coordinateXY;
    }
}
