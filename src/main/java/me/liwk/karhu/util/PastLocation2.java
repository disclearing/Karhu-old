package me.liwk.karhu.util;

import org.bukkit.Location;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PastLocation2 {
    public List<CustomLocation2> previousLocations = new CopyOnWriteArrayList<>();

    public CustomLocation2 getPreviousLocation(long time) {
        return this.previousLocations.stream()
                .min(Comparator.comparing(loc -> Long.valueOf(Math.abs(time - loc.timeStamp))))
                .orElse(this.previousLocations.get(0));
    }

    public List<CustomLocation2> getEstimatedLocation(long time, long ping, long delta) {
        return (List<CustomLocation2>)this.previousLocations
                .stream()
                .filter(loc -> (time - loc.timeStamp > 0L && time - loc.timeStamp < ping + delta))
                .collect(Collectors.toList());
    }

    public List<CustomLocation2> getPreviousRange(long delta) {
        long stamp = System.currentTimeMillis();
        return (List<CustomLocation2>)this.previousLocations.stream()
                .filter(loc -> (stamp - loc.timeStamp < delta))
                .collect(Collectors.toList());
    }

    public void addLocation(Location location) {
        if (this.previousLocations.size() >= 20)
            this.previousLocations.remove(0);
        this.previousLocations.add(new CustomLocation2(location));
    }

    public CustomLocation2 getLast() {
        if (this.previousLocations.size() == 0)
            return null;
        return this.previousLocations.get(this.previousLocations.size() - 1);
    }

    public CustomLocation2 getFirst() {
        if (this.previousLocations.size() == 0)
            return null;
        return this.previousLocations.get(0);
    }

    public void addLocation(CustomLocation2 location) {
        if (this.previousLocations.size() >= 20)
            this.previousLocations.remove(0);
        this.previousLocations.add(location.clone());
    }
}