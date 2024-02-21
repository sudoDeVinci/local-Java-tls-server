package httpsserver.db.entities;


public class DeviceEntity extends Entity{
    private String name;
    private final String devModel;
    private String camModel;
    private double altitude;
    private double latitude;
    private double longitude;

    public DeviceEntity(String MAC, String name, String devModel, String camModel, double altitude, double latitude, double longitude) {
        super(MAC, null);
        this.name = name;
        this.devModel = devModel;
        this.camModel = camModel;
        this.altitude = altitude;
        this.latitude = latitude;
        this.longitude = longitude;   
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getDevModel() {
        return devModel;
    }

    public String getCamModel() {
        return camModel;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setName(String name) {
        this.name = name;
    }
}
