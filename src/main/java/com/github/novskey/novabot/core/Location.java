package com.github.novskey.novabot.core;

import com.github.novskey.novabot.maps.GeofenceIdentifier;
import lombok.Data;

import java.util.ArrayList;

import static com.github.novskey.novabot.Util.StringLocalizer.getLocalString;

@Data
public class Location {
    public static final Location ALL = new Location();
    public static String all;
    public final LocationType locationType;
    public ArrayList<GeofenceIdentifier> geofenceIdentifiers = new ArrayList<>();
    private String suburb;

    private Location(final String suburb) {
        this.locationType = LocationType.Suburb;
        this.suburb = suburb;
    }

    public Location(final GeofenceIdentifier geofenceIdentifier) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = new ArrayList<>();
        this.geofenceIdentifiers.add(geofenceIdentifier);
    }


    public Location(ArrayList<GeofenceIdentifier> identifiers) {
        this.locationType = LocationType.Geofence;
        this.geofenceIdentifiers = identifiers;
    }

    private Location() {
        this.locationType = LocationType.All;
    }

    public static Location fromDbString(String str, NovaBot novaBot) {
        if (novaBot.getConfig().isAllowAllLocation() && str.equals("all")) return Location.ALL;

        if (novaBot.getConfig().useGeofences()) {
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if (identifiers.size() != 0) {
                return new Location(identifiers);
            }
        }

        if (novaBot.getSuburbs().isSuburb(str)) {
            return new Location(str);
        }
        if (str.equals("civic")) {
            return new Location("city");
        }
        novaBot.novabotLog.warn(str + ", from db string is null");
        return null;
    }

    public static Location fromString(final String str, NovaBot novaBot) {

        if (novaBot.getConfig().isAllowAllLocation() && str.equalsIgnoreCase(getLocalString("All"))) return Location.ALL;

        if (novaBot.getConfig().useGeofences()) {
            ArrayList<GeofenceIdentifier> identifiers = GeofenceIdentifier.fromString(str);

            if (identifiers.size() != 0) {
                return new Location(identifiers);
            }
        }

        if (novaBot.getSuburbs().isSuburb(str)) {
            return new Location(str);
        }
        return null;
    }

    public static String listToString(final Location[] locations) {
        StringBuilder str = new StringBuilder();
        if (locations.length == 1) {
            return locations[0].toWords();
        }
        for (int i = 0; i < locations.length; ++i) {
            if (i == locations.length - 1) {
                str.append("and ").append(locations[i].toWords());
            } else {
                str.append((i == locations.length - 2) ? (locations[i].toWords() + " ") : (locations[i].toWords() + ", "));
            }
        }
        return str.toString();
    }

    public String toDbString() {

        switch (locationType) {
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
            case All:
                return "all";
        }

        return null;
    }

    @Override
    public String toString() {

        switch (locationType) {
            case Suburb:
                return this.suburb;
            case Geofence:
                return GeofenceIdentifier.listToString(this.geofenceIdentifiers);
            case All:
                return getLocalString("All");
        }

        return "";
    }

    public String toWords() {
        return toString();
    }
}
