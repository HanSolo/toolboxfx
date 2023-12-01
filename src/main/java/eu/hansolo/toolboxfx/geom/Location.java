/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2016-2021 Gerrit Grunwald.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.toolboxfx.geom;

import eu.hansolo.toolbox.Helper;
import eu.hansolo.toolbox.evt.Evt;
import eu.hansolo.toolbox.evt.EvtObserver;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.geo.GeoLocation;
import eu.hansolo.toolbox.geo.GeoLocationBuilder;
import eu.hansolo.toolboxfx.Constants;
import eu.hansolo.toolboxfx.HelperFX;
import eu.hansolo.toolboxfx.evt.type.LocationChangeEvt;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Location {
    private       GeoLocation                                        geoLocation;
    private       Color                                              _fill;
    private       ObjectProperty<Color>                              fill;
    private       Color                                              _stroke;
    private       ObjectProperty<Color>                              stroke;
    private       int                                                zoomLevel;
    private       Location                                           oldLocation;
    private       EventHandler<MouseEvent>                           mouseEnterHandler;
    private       EventHandler<MouseEvent>                           mousePressHandler;
    private       EventHandler<MouseEvent>                           mouseReleaseHandler;
    private       EventHandler<MouseEvent>                           mouseExitHandler;
    private       Map<EvtType, List<EvtObserver<LocationChangeEvt>>> observers;


    // ******************** Constructors **************************************
    public Location() {
        this(Instant.now(), 0, 0, 0, 1, "", "", Color.BLUE, Color.TRANSPARENT);
    }
    public Location (final double latitude, final double longitude) {
        this(Instant.now(), latitude, longitude, 0, 1, "", "", Color.BLUE, Color.TRANSPARENT);
    }
    public Location(final Instant timestamp, final double latitude, final double longitude, final double altitude, final double accuracy, final String name, final String info, final Color fill, final Color stroke) {
        this.geoLocation = GeoLocationBuilder.create()
                                             .name(name)
                                             .info(info)
                                             .timestamp(timestamp.getEpochSecond())
                                             .latitude(latitude)
                                             .longitude(longitude)
                                             .altitude(altitude)
                                             .accuracy(accuracy)
                                             .build();

        this.zoomLevel   = 15;
        this._fill       = fill;
        this._stroke     = stroke;
        this.oldLocation = null;
        this.observers   = new ConcurrentHashMap<>();
    }


    // ******************** Methods *******************************************
    public String getId() { return this.geoLocation.getId(); }

    public Instant getTimestamp() { return Instant.ofEpochSecond(geoLocation.getTimestamp()); }
    public long getTimestampInSeconds() { return geoLocation.getTimestamp(); }
    public void setTimestamp(final Instant timestamp) {
        this.oldLocation = getCopy();
        this.geoLocation.setTimestamp(timestamp.getEpochSecond());
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.TIMESTAMP_CHANGED, oldLocation, Location.this));
    }

    public LocalDateTime getLocaleDateTime() { return getLocalDateTime(ZoneId.systemDefault()); }
    public LocalDateTime getLocalDateTime(final ZoneId zoneId) {
        return LocalDateTime.ofInstant(getTimestamp(), zoneId);
    }

    public double getLatitude() { return this.geoLocation.getLatitude(); }
    public void setLatitude(final double latitude) {
        this.oldLocation = getCopy();
        this.geoLocation.setLatitude(latitude);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getLongitude() { return this.geoLocation.getLongitude(); }
    public void setLongitude(final double longitude) {
        final Location oldLocation = getCopy();
        this.geoLocation.setLongitude(longitude);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getAltitude() { return this.geoLocation.getAltitude(); }
    public void setAltitude(final double altitude) {
        this.oldLocation = getCopy();
        this.geoLocation.setAltitude(altitude);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getAccuracy() { return this.geoLocation.getAccuracy(); }
    public void setAccuracy(final double accuracy) {
        this.oldLocation = getCopy();
        this.geoLocation.setAccuracy(accuracy);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.ACCURACY_CHANGED, oldLocation, Location.this));
    }

    public String getName() { return this.geoLocation.getName(); }
    public void setName(final String name) {
        this.oldLocation = getCopy();
        this.geoLocation.setName(name);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.NAME_CHANGED, oldLocation, Location.this));
    }

    public String getInfo() { return this.geoLocation.getInfo(); }
    public void setInfo(final String info) {
        this.oldLocation = getCopy();
        this.geoLocation.setInfo(info);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.INFO_CHANGED, oldLocation, Location.this));
    }

    public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color fill) {
        oldLocation = getCopy();
        if (null == this.fill) {
            _fill       = fill;
            fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.FILL_CHANGED, oldLocation, Location.this));
        } else {
            this.fill.set(fill);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<>(_fill) {
                @Override protected void invalidated() {
                    fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.FILL_CHANGED, oldLocation, Location.this));
                }
                @Override public void set(final Color color) {
                    oldLocation = getCopy();
                    super.set(color);
                }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color stroke) {
        oldLocation = getCopy();
        if (null == this.stroke) {
            _stroke     = stroke;
            fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.STROKE_CHANGED, oldLocation, Location.this));
        } else {
            this.stroke.set(stroke);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<>(_stroke) {
                @Override protected void invalidated() {
                    fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.STROKE_CHANGED, oldLocation, Location.this));
                }
                @Override public void set(final Color color) {
                    oldLocation = getCopy();
                    super.set(color);
                }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
    }

    public int getZoomLevel() { return zoomLevel; }
    public void setZoomLevel(final int level) {
        this.oldLocation = getCopy();
        this.zoomLevel   = Helper.clamp(0, 17, level);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.ZOOM_LEVEL_CHANGED, oldLocation, Location.this));
    }

    // longitude -> x and latitude -> y
    public Point getAsPoint() { return new Point(this.geoLocation.getLongitude(), this.geoLocation.getLatitude()); }

    public GeoLocation getGeoLocation() { return this.geoLocation; }

    public void set(final double latitude, final double longitude) {
        final Location oldLocation = getCopy();
        this.geoLocation.set(latitude, longitude);
        this.geoLocation.setTimestamp(Instant.now().getEpochSecond());
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp) {
        final Location oldLocation = getCopy();
        this.geoLocation.set(latitude, longitude, altitude, Instant.now().getEpochSecond());
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final double latitude, final double longitude, final double altitude, final Instant timestamp, final double accuracy, final String info) {
        final Location oldLocation = getCopy();
        this.geoLocation.set(latitude, longitude, altitude, timestamp.getEpochSecond(), accuracy, info);
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }
    public void set(final Location location) {
        final Location oldLocation = getCopy();
        this.geoLocation.set(location.getLatitude(), location.getLongitude(), location.getAltitude(), location.getTimestamp().getEpochSecond(), location.getAccuracy(), location.getInfo());
        this.geoLocation.setName(location.getName());
        fireLocationEvent(new LocationChangeEvt(Location.this, LocationChangeEvt.LOCATION_CHANGED, oldLocation, Location.this));
    }

    public double getDistanceTo(final Location location) { return calcDistanceInMeter(this, location); }

    public boolean isWithinRangeOf(final Location location, final double meters) { return getDistanceTo(location) < meters; }

    public static double calcDistanceInMeter(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude());
    }
    public static double calcDistanceInKilometer(final Location p1, final Location p2) {
        return calcDistanceInMeter(p1, p2) / 1000.0;
    }
    public static double calcDistanceInMeter(final double lat1, final double lon1, final double lat2, final double lon2) {
        final double lat1Radians     = Math.toRadians(lat1);
        final double lat2Radians     = Math.toRadians(lat2);
        final double deltaLatRadians = Math.toRadians(lat2 - lat1);
        final double deltaLonRadians = Math.toRadians(lon2 - lon1);

        final double a = Math.sin(deltaLatRadians * 0.5) * Math.sin(deltaLatRadians * 0.5) + Math.cos(lat1Radians) * Math.cos(lat2Radians) * Math.sin(deltaLonRadians * 0.5) * Math.sin(deltaLonRadians * 0.5);
        final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        final double distance = Constants.EARTH_RADIUS * c;

        return distance;
    }

    public double getAltitudeDifferenceInMeter(final Location location) { return (this.geoLocation.getAltitude() - location.getAltitude()); }

    public double getBearingTo(final Location location) {
        return calcBearingInDegree(getLatitude(), getLongitude(), location.getLatitude(), location.getLongitude());
    }
    public double getBearingTo(final double latitude, final double longitude) {
        return calcBearingInDegree(getLatitude(), getLongitude(), latitude, longitude);
    }

    public boolean isZero() { return Double.compare(this.geoLocation.getLatitude(), 0d) == 0 && Double.compare(this.geoLocation.getLongitude(), 0d) == 0; }

    public double calcBearingInDegree(final double lt1, final double ln1, final double lt2, final double ln2) {
        double lat1     = Math.toRadians(lt1);
        double lon1     = Math.toRadians(ln1);
        double lat2     = Math.toRadians(lt2);
        double lon2     = Math.toRadians(ln2);
        double deltaLon = lon2 - lon1;
        double deltaPhi = Math.log(Math.tan(lat2 * 0.5 + Math.PI * 0.25) / Math.tan(lat1 * 0.5 + Math.PI * 0.25));
        if (Math.abs(deltaLon) > Math.PI) {
            if (deltaLon > 0) {
                deltaLon = -(2.0 * Math.PI - deltaLon);
            } else {
                deltaLon = (2.0 * Math.PI + deltaLon);
            }
        }
        double bearing = (Math.toDegrees(Math.atan2(deltaLon, deltaPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public String getCardinalDirectionFromBearing(final double brng) {
        double bearing = brng % 360.0;
        for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
            if (Double.compare(bearing, cardinalDirection.from) >= 0 && Double.compare(bearing, cardinalDirection.to) < 0) {
                return cardinalDirection.direction;
            }
        }
        return "";
    }

    public Location getCopy() {
        return new Location(Instant.ofEpochSecond(this.geoLocation.getTimestamp()), this.geoLocation.getLatitude(), this.geoLocation.getLongitude(), this.geoLocation.getAltitude(), this.geoLocation.getAccuracy(), this.geoLocation.getName(), this.geoLocation.getInfo(), getFill(), getStroke());
    }

    public void dispose() { removeAllObservers(); }


    // ******************** Event handling ************************************
    public void addLocationObserver(final EvtType<? extends Evt> type, final EvtObserver<LocationChangeEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeLocationObserver(final EvtType<? extends Evt> type, final EvtObserver<LocationChangeEvt> observer) {
        if (observers.containsKey(type) && observers.get(type).contains(observer)) {
            observers.get(type).remove(observer);
        }
    }
    public void removeAllObservers() { observers.clear(); }

    public void fireLocationEvent(final LocationChangeEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(LocationChangeEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    public EventHandler<MouseEvent> getMouseEnterHandler() { return mouseEnterHandler; }
    public void setMouseEnterHandler(final EventHandler<MouseEvent> handler) { mouseEnterHandler = handler; }

    public EventHandler<MouseEvent> getMousePressHandler() { return mousePressHandler; }
    public void setMousePressHandler(final EventHandler<MouseEvent> handler) { mousePressHandler = handler; }

    public EventHandler<MouseEvent> getMouseReleaseHandler() { return mouseReleaseHandler; }
    public void setMouseReleaseHandler(final EventHandler<MouseEvent> handler) { mouseReleaseHandler = handler;  }

    public EventHandler<MouseEvent> getMouseExitHandler() { return mouseExitHandler; }
    public void setMouseExitHandler(final EventHandler<MouseEvent> handler) { mouseExitHandler = handler; }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object other) {
        if (other instanceof Location) {
            final Location location = (Location) other;
            return this.geoLocation.getId().equals(location.getId());
        } else {
            return false;
        }
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = this.geoLocation.getName() != null ? this.geoLocation.getName().hashCode() : 0;
        temp = Double.doubleToLongBits(this.geoLocation.getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.geoLocation.getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.geoLocation.getAltitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("id").append(QUOTES).append(COLON).append(this.geoLocation.getId()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("timestamp").append(QUOTES).append(COLON).append(this.geoLocation.getTimestamp()).append(COMMA)
                                  .append(QUOTES).append("latitude").append(QUOTES).append(COLON).append(this.geoLocation.getLatitude()).append(COMMA)
                                  .append(QUOTES).append("longitude").append(QUOTES).append(COLON).append(this.geoLocation.getLongitude()).append(COMMA)
                                  .append(QUOTES).append("altitude").append(QUOTES).append(COLON).append(this.geoLocation.getAltitude()).append(COMMA)
                                  .append(QUOTES).append("accuracy").append(QUOTES).append(COLON).append(this.geoLocation.getAccuracy()).append(COMMA)
                                  .append(QUOTES).append("name").append(QUOTES).append(COLON).append(QUOTES).append(this.geoLocation.getName()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("info").append(QUOTES).append(COLON).append(QUOTES).append(this.geoLocation.getInfo()).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("fill").append(QUOTES).append(COLON).append(QUOTES).append(HelperFX.colorToWeb(getFill())).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("stroke").append(QUOTES).append(COLON).append(QUOTES).append(HelperFX.colorToWeb(getStroke())).append(QUOTES).append(COMMA)
                                  .append(QUOTES).append("zoom_level").append(QUOTES).append(COLON).append(zoomLevel)
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
