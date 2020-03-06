package pt.ipleiria.estg.ei.taes.sentinel;

import java.util.Calendar;
import java.util.Date;

public class SensorData {
    private double temperature;
    private double humidity;
    private Local local;
    private String timeStamp;


    public SensorData(double temperature, double humidity, Local local, String timeStamp) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.local = local;
        this.timeStamp = timeStamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public Local getLocal() {
        return local;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
