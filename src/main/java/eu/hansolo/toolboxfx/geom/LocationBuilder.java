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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.util.HashMap;


public class LocationBuilder<B extends LocationBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected LocationBuilder() {}


    // ******************** Methods *******************************************
    public static final LocationBuilder create() {
        return new LocationBuilder();
    }

    public final B name(final String name) {
        properties.put("name", new SimpleStringProperty(name));
        return (B) this;
    }

    public final B timestamp(final Instant timestamp) {
        properties.put("timestamp", new SimpleObjectProperty<>(timestamp));
        return (B) this;
    }

    public final B latitude(final double latitude) {
        properties.put("latitude", new SimpleDoubleProperty(latitude));
        return (B) this;
    }

    public final B longitude(final double longitude) {
        properties.put("longitude", new SimpleDoubleProperty(longitude));
        return (B) this;
    }

    public final B altitude(final double altitude) {
        properties.put("altitude", new SimpleDoubleProperty(altitude));
        return (B) this;
    }

    public final B accuracy(final double accuracy) {
        properties.put("accuracy", new SimpleDoubleProperty(accuracy));
        return (B)this;
    }

    public final B info(final String info) {
        properties.put("info", new SimpleStringProperty(info));
        return (B) this;
    }

    public final B fill(final Color fill) {
        properties.put("fill", new SimpleObjectProperty(fill));
        return (B) this;
    }

    public final B stroke(final Color stroke) {
        properties.put("stroke", new SimpleObjectProperty(stroke));
        return (B) this;
    }

    public final B zoomLevel(final int level) {
        properties.put("zoomLevel", new SimpleIntegerProperty(level));
        return (B) this;
    }

    public final Location build() {
        Location location = new Location();
        properties.forEach((key, property) -> {
            if ("name".equals(key)) {
                location.setName(((StringProperty) properties.get(key)).get());
            } else if ("timestamp".equals(key)) {
                location.setTimestamp(((ObjectProperty<Instant>) properties.get(key)).get());
            } else if ("latitude".equals(key)) {
                location.setLatitude(((DoubleProperty) properties.get(key)).get());
            } else if ("longitude".equals(key)) {
                location.setLongitude(((DoubleProperty) properties.get(key)).get());
            } else if ("altitude".equals(key)) {
                location.setAltitude(((DoubleProperty) properties.get(key)).get());
            } else if ("accuracy".equals(key)) {
                location.setAccuracy(((DoubleProperty) properties.get(key)).get());
            } else if ("info".equals(key)) {
                location.setInfo(((StringProperty) properties.get(key)).get());
            } else if ("fill".equals(key)) {
                location.setFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("stroke".equals(key)) {
                location.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("zoomLevel".equals(key)) {
                location.setZoomLevel(((IntegerProperty) properties.get(key)).get());
            }

        });
        return location;
    }
}
