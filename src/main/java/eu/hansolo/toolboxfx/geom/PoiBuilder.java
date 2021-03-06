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

import eu.hansolo.toolboxfx.ValueObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.HashMap;


public class PoiBuilder<B extends PoiBuilder<B>> {
    private HashMap<String, Property> properties = new HashMap<>();


    // ******************** Constructors **************************************
    protected PoiBuilder() {}


    // ******************** Methods *******************************************
    public static final PoiBuilder create() {
        return new PoiBuilder();
    }

    public final B lat(final double latitude) {
        properties.put("lat", new SimpleDoubleProperty(latitude));
        return (B) this;
    }

    public final B lon(final double longitude) {
        properties.put("lon", new SimpleDoubleProperty(longitude));
        return (B) this;
    }

    public final B name(final String name) {
        properties.put("name", new SimpleStringProperty(name));
        return (B) this;
    }

    public final B info(final String info) {
        properties.put("info", new SimpleStringProperty(info));
        return (B) this;
    }

    public final B valueObject(final ValueObject valueObject) {
        properties.put("valueObject", new SimpleObjectProperty<>(valueObject));
        return (B)this;
    }

    public final B pointSize(final PoiSize poiSize) {
        properties.put("poiSize", new SimpleObjectProperty<>(poiSize));
        return (B)this;
    }

    public final B fill(final Color fill) {
        properties.put("fill", new SimpleObjectProperty(fill));
        return (B) this;
    }

    public final B stroke(final Color stroke) {
        properties.put("stroke", new SimpleObjectProperty(stroke));
        return (B) this;
    }

    public final B image(final Image image) {
        properties.put("image", new SimpleObjectProperty<>(image));
        return (B)this;
    }

    public final B svgPath(final String svgPath) {
        properties.put("svgPath", new SimpleStringProperty(svgPath));
        return (B)this;
    }

    public final B svgPathDim(final Dimension2D svgPathDim) {
        properties.put("svgPathDim", new SimpleObjectProperty<>(svgPathDim));
        return (B)this;
    }

    public final Poi build() {
        Poi poi = new Poi();
        properties.forEach((key, property) -> {
            if ("lat".equals(key)) {
                poi.setLat(((DoubleProperty) properties.get(key)).get());
            } else if ("lon".equals(key)) {
                poi.setLon(((DoubleProperty) properties.get(key)).get());
            } else if ("name".equals(key)) {
                poi.setName(((StringProperty) properties.get(key)).get());
            } else if ("info".equals(key)) {
                poi.setInfo(((StringProperty) properties.get(key)).get());
            } else if ("valueObject".equals(key)) {
                poi.setValueObject(((ObjectProperty<ValueObject>) properties.get(key)).get());
            } else if ("poiSize".equals(key)) {
                poi.setPoiSize(((ObjectProperty<PoiSize>) properties.get(key)).get());
            } else if ("fill".equals(key)) {
                poi.setFill(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("stroke".equals(key)) {
                poi.setStroke(((ObjectProperty<Color>) properties.get(key)).get());
            } else if ("image".equals(key)) {
                poi.setImage(((ObjectProperty<Image>) properties.get(key)).get());
            } else if ("svgPath".equals(key)) {
                poi.setSvgPath(((StringProperty) properties.get(key)).get());
            } else if ("svgPathDim".equals(key)) {
                poi.setSvgPathDim(((ObjectProperty<Dimension2D>) properties.get(key)).get());
            }
        });
        return poi;
    }
}
