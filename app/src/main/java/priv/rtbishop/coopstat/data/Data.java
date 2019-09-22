package priv.rtbishop.coopstat.data;

public class Data {

    private String currentHumid, currentTemp;
    private boolean isFanOn, isHeaterOn, isLightOn;

    public Data(String currentHumid, String currentTemp,
                boolean isFanOn, boolean isHeaterOn, boolean isLightOn) {
        this.currentHumid = currentHumid;
        this.currentTemp = currentTemp;
        this.isFanOn = isFanOn;
        this.isHeaterOn = isHeaterOn;
        this.isLightOn = isLightOn;
    }

    public String getCurrentHumid() {
        return currentHumid;
    }

    public String getCurrentTemp() {
        return currentTemp;
    }

    public boolean isFanOn() {
        return isFanOn;
    }

    public boolean isHeaterOn() {
        return isHeaterOn;
    }

    public boolean isLightOn() {
        return isLightOn;
    }
}
