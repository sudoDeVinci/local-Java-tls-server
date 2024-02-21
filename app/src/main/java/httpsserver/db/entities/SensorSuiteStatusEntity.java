package httpsserver.db.entities;

import java.sql.Timestamp;

public class SensorSuiteStatusEntity extends Entity{

    private boolean shtStat;
    private boolean bmpStat;
    private boolean camStat;
    private boolean wifiStat;

    public SensorSuiteStatusEntity(String MAC, boolean shtStat, boolean bmpStat, boolean camStat, boolean wifiStat, Timestamp timestamp) {
        super(MAC, timestamp);
        this.shtStat = shtStat;
        this.bmpStat = bmpStat;
        this.camStat = camStat;
        this.wifiStat = wifiStat;
    }

    public boolean SHT() {
        return shtStat;
    }

    public boolean BMP() {
        return bmpStat;
    }

    public boolean CAM() {
        return camStat;
    }

    public boolean WIFI() {
        return wifiStat;
    }

    public void setSHT(boolean b) {
        this.shtStat = b;
    }

    public void setBMP(boolean b) {
        this.bmpStat = b;
    }

    public void CAM(boolean b) {
        this.camStat = b;
    }

    public void setWIFI(boolean b) {
        this.wifiStat = b;
    }

    public boolean allUp() {
        return wifiStat&&camStat&&bmpStat&&shtStat;
    }

}
