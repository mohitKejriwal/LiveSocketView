package com.india.android.websocketio;

/**
 * Created by admin on 02-09-2018.
 */

public class StockData {
    long epoch;
    float openValue,closeValue,highestValue,lowestValue;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public float getOpenValue() {
        return openValue;
    }

    public void setOpenValue(float openValue) {
        this.openValue = openValue;
    }

    public float getCloseValue() {
        return closeValue;
    }

    public void setCloseValue(float closeValue) {
        this.closeValue = closeValue;
    }

    public float getHighestValue() {
        return highestValue;
    }

    public void setHighestValue(float highestValue) {
        this.highestValue = highestValue;
    }

    public float getLowestValue() {
        return lowestValue;
    }

    public void setLowestValue(float lowestValue) {
        this.lowestValue = lowestValue;
    }
}
