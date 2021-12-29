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
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class Poi {
    private double      lat;
    private double      lon;
    private String      name;
    private String      info;
    private ValueObject valueObject;
    private PoiSize     poiSize;
    private Color       fill;
    private Color       stroke;
    private Image       image;
    private String      svgPath;
    private Dimension2D svgPathDim;


    // ******************** Constructors **************************************
    public Poi() {
        this(0, 0, "", "", null, PoiSize.NORMAL, null, null, null, null, null);
    }
    public Poi(final double lat, final double lon, final String name, final String info, final ValueObject valueObject, final PoiSize poiSize, final Color fill, final Color stroke, final Image image, final String svgPath, final Dimension2D svgPathDim) {
        this.lat         = lat;
        this.lon         = lon;
        this.name        = name;
        this.info        = info;
        this.valueObject = valueObject;
        this.poiSize     = poiSize;
        this.fill        = fill;
        this.stroke      = stroke;
        this.image       = image;
        this.svgPath     = svgPath;
        this.svgPathDim  = svgPathDim;

        if (null == svgPathDim && svgPath != null) { throw new IllegalArgumentException("svgPathDim cannot be null"); }
    }


    // ******************** Methods *******************************************
    public double getLat() { return lat; }
    public void setLat(final double lat) { this.lat = lat; }

    public double getLon() { return lon; }
    public void setLon(final double lon) { this.lon = lon; }

    public Point getLonLat() { return new Point(lon, lat); }
    public void setLonLat(final Point lonlat) {
        this.lon = lonlat.getX();
        this.lat = lonlat.getY();
    }

    public Point getLatLon() { return new Point(lat, lon); }
    public void setLatLon(final Point latlon) {
        this.lat = latlon.getY();
        this.lon = latlon.getX();
    }

    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getInfo() { return info; }
    public void setInfo(final String info) { this.info = info; }

    public ValueObject getValueObject() { return valueObject; }
    public void setValueObject(final ValueObject valueObject) { this.valueObject = valueObject; }

    public PoiSize getPoiSize() { return poiSize; }
    public void setPoiSize(final PoiSize poiSize) { this.poiSize = poiSize; }

    public Color getFill() { return fill; }
    public void setFill(final Color fill) { this.fill = fill; }

    public Color getStroke() { return stroke; }
    public void setStroke(final Color stroke) { this.stroke = stroke; }

    public Image getImage() { return image; }
    public void setImage(final Image image) { this.image = image; }

    public String getSvgPath() { return svgPath; }
    public void setSvgPath(final String svgPath) { this.svgPath = svgPath; }

    public Dimension2D getSvgPathDim() { return svgPathDim; }
    public void setSvgPathDim(final Dimension2D svgPathDim) { this.svgPathDim = svgPathDim; }

    public Location toLocation() {
        return LocationBuilder.create().name(getName()).fill(getFill()).stroke(getStroke()).latitude(getLat()).longitude(getLon()).build();
    }

    public static Poi fromLocation(final Location location) {
        return new Poi(location.getLatitude(), location.getLongitude(), location.getName(), "", null, PoiSize.NORMAL, location.getFill(), location.getStroke(), null, null, null);
    }
}