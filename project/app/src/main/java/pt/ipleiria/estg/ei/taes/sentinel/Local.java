package pt.ipleiria.estg.ei.taes.sentinel;

public class Local {
    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private  String humidity;
    private  String temperature;
    private String escola;
    private String edificio;
    private String sala;
    private String status;
    private String timeStamp;

    public String getStatus() {
        return status;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public Local(String escola, String edificio, String sala) {
        this.escola = escola;
        this.edificio = edificio;
        this.sala = sala;
    }

    public Local(String escola, String edificio) {
        this.escola = escola;
        this.edificio = edificio;
        this.sala = null;
    }

    public Local(String school, String building, String room, String status,String timeStamp,String humidity,String temperature) {
        this.escola = school;
        this.edificio = building;
        this.sala = room;
        this.status=status;
        this.timeStamp=timeStamp;
        this.temperature=temperature;
        this.humidity=humidity;

    }

    public String getEscola() {
        return escola;
    }

    public void setEscola(String escola) {
        this.escola = escola;
    }

    public String getEdificio() {
        return edificio;
    }

    public void setEdificio(String edificio) {
        this.edificio = edificio;
    }

    public String getSala() {
        return sala;
    }

    public void setSala(String sala) {
        this.sala = sala;
    }
}
