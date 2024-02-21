package httpsserver.db.entities;

import java.sql.Timestamp;

public class Entity {
    private final String MAC;
    private final Timestamp timestamp;

    public Entity(String MAC,  Timestamp timestamp) {
        this.MAC = MAC;
        this.timestamp = timestamp;
    }

    public String getMAC() {
        return MAC;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Entity) {
            return getMAC().equals(((Entity) o).getMAC());
        }

        return super.equals(o);
    }
}
