package org.devathon.contest2016.block;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

/**
 * Created by Florian on 05.11.16 in org.devathon.contest2016.block
 */
public class SerializeableLocation implements Serializable {

    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public SerializeableLocation(Location location) {
        worldName = location.getWorld().getName();
        x = location.getX();
        y = location.getY();
        z = location.getZ();
        yaw = location.getYaw();
        pitch = location.getPitch();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerializeableLocation that = (SerializeableLocation) o;

        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0 && Double.compare(that.z, z) == 0
                && Float.compare(that.yaw, yaw) == 0 && Float.compare(that.pitch, pitch) == 0
                && (worldName != null ? worldName.equals(that.worldName) : that.worldName == null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = worldName != null ? worldName.hashCode() : 0;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (yaw != +0.0f ? Float.floatToIntBits(yaw) : 0);
        result = 31 * result + (pitch != +0.0f ? Float.floatToIntBits(pitch) : 0);
        return result;
    }
}
