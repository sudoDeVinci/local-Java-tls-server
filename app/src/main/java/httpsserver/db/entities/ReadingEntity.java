package httpsserver.db.entities;

import java.sql.Timestamp;

public class ReadingEntity extends Entity{
    private final double temp;
    private final double humidity;
    private final double pressure;
    private final double dewpoint;
    private String imagePath;

    public ReadingEntity(String MAC, double temp, double humidity, double pressure, double dewpoint, Timestamp timestamp, String imagePath) {
        super(MAC, timestamp);
        this.temp = temp;
        this.humidity = humidity;
        this.pressure = pressure;
        this.dewpoint = dewpoint;
        this.imagePath = imagePath;
    }

    public double getTemp() {
        return temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public double pressure() {
        return pressure;
    }

    public double getDewpoint () {
        return dewpoint;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String path) {
        this.imagePath = path;
    }


}
