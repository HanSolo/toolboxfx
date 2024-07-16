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

package eu.hansolo.toolboxfx;

import eu.hansolo.toolbox.Helper;
import javafx.animation.Interpolator;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


public class ConicalGradient {
    private static final double ANGLE_FACTOR = 1.0 / 360.0;
    private double              centerX;
    private double              centerY;
    private List<Stop>          sortedStops;
    private ScaleDirection      scaleDirection;
    private WritableImage       rectRaster;
    private WritableImage       roundRaster;


    // ******************** Constructors **************************************
    public ConicalGradient() {
        this(0, 0, 0, ScaleDirection.CLOCKWISE, Arrays.asList(new Stop[]{}));
    }
    public ConicalGradient(final Stop... stops) {
        this(0, 0, 0, ScaleDirection.CLOCKWISE, Arrays.asList(stops));
    }
    public ConicalGradient(final List<Stop> stops) {
        this(0, 0, 0, ScaleDirection.CLOCKWISE, stops);
    }
    public ConicalGradient(final double centerX, final double centerY, final Stop... stops) { this(centerX, centerY, ScaleDirection.CLOCKWISE, stops); }
    public ConicalGradient(final double centerX, final double centerY, final ScaleDirection direction, final Stop... stops) {
        this(centerX, centerY, 0.0, direction, Arrays.asList(stops));
    }
    public ConicalGradient(final double centerX, final double centerY, final ScaleDirection direction, final List<Stop> stops) {
        this(centerX, centerY, 0.0, direction, stops);
    }
    public ConicalGradient(final double centerX, final double centerY, final double offset, final Stop... stops) {
        this(centerX, centerY, offset, ScaleDirection.CLOCKWISE, Arrays.asList(stops));
    }
    public ConicalGradient(final double centerX, final double centerY, final double offset, final ScaleDirection direction, final Stop... stops) {
        this(centerX, centerY, offset, direction, Arrays.asList(stops));
    }
    public ConicalGradient(final double centerX, final double centerY, final double offset, final ScaleDirection direction, final List<Stop> stops) {
        this.centerX        = centerX;
        this.centerY        = centerY;
        this.scaleDirection = direction;
        this.sortedStops    = normalizeStops(offset, stops);
    }


    // ******************** Methods *******************************************
    public void recalculateWithAngle(final double angle) {
        double angl = angle % 360.0;
        sortedStops  = calculate(sortedStops, ANGLE_FACTOR * angl);
        rectRaster   = null;
        roundRaster  = null;
    }

    public List<Stop> getStops() { return sortedStops; }
    public void setStops(final Stop... stops) {
        setStops(Arrays.asList(stops));
    }
    public void setStops(final double offset, final Stop... stops) {
        setStops(offset, Arrays.asList(stops));
    }
    public void setStops(final List<Stop> stops) {
        setStops(0 ,stops);
    }
    public void setStops(final double offset, final List<Stop> stops) {
        sortedStops = normalizeStops(offset, stops);
        rectRaster  = null;
        roundRaster = null;
    }

    public double[] getCenter() { return new double[]{ centerX, centerY }; }
    public double getCenterX() { return centerX; }
    public double getCenterY() { return centerY; }
    public Point2D getCenterPoint() { return new Point2D(centerX, centerY); }

    public Image getImage(final double width, final double height) {
        int w  = (int) width  <= 0 ? 100 : (int) width;
        int h = (int) height <= 0 ? 100 : (int) height;

        if (rectRaster != null && w == rectRaster.getWidth() && h == rectRaster.getHeight()) return rectRaster;

        Color color = Color.TRANSPARENT;
        rectRaster  = new WritableImage(w, h);
        final PixelWriter PIXEL_WRITER = rectRaster.getPixelWriter();
        if (Double.compare(0.0, centerX) == 0) centerX = w * 0.5;
        if (Double.compare(0.0, centerY) == 0) centerY = h * 0.5;

        int calculatedStopsLength = sortedStops.size() - 1;
        for (int y = 0 ; y < h ; y++) {
            for (int x = 0 ; x < w ; x++) {
                double dx       = x - centerX;
                double dy       = y - centerY;
                double distance = Math.sqrt((dx * dx) + (dy * dy));
                distance = Double.compare(distance, 0) == 0 ? 1 : distance;

                double angle = adjustAngle(dx, dy, Math.abs(Math.toDegrees(Math.acos(dx / distance))));

                for (int i = 0; i < calculatedStopsLength; i++) {
                    double offsetI      = (sortedStops.get(i).getOffset() * 360.0);
                    double offsetIPlus1 = (sortedStops.get(i + 1).getOffset() * 360.0);
                    if (Double.compare(angle, offsetI) >= 0 &&
                        Double.compare(angle, offsetIPlus1) < 0) {
                        double fraction = (angle - offsetI) / (offsetIPlus1 - offsetI);
                        color = (Color) Interpolator.LINEAR.interpolate(sortedStops.get(i).getColor(), sortedStops.get(i + 1).getColor(), fraction);
                    }
                }
                PIXEL_WRITER.setColor(x, y, color);
            }
        }
        return rectRaster;
    }
    public Image getRoundImage(final double size) {
        int s  = (int) size  <= 0 ? 100 : (int) size;

        if (roundRaster != null && s == roundRaster.getWidth()) return roundRaster;

        Color color = Color.TRANSPARENT;
        roundRaster = new WritableImage(s, s);
        final PixelWriter   PIXEL_WRITER = roundRaster.getPixelWriter();
        if (Double.compare(0.0, centerX) == 0) centerX = s * 0.5;
        if (Double.compare(0.0, centerY) == 0) centerY = s * 0.5;
        double radius                = s * 0.5;
        int    calculatedStopsLength = sortedStops.size() - 1;
        for (int y = 0; y < s; y++) {
            for (int x = 0; x < s; x++) {
                double dx       = x - centerX;
                double dy       = y - centerY;
                double distance = Math.sqrt((dx * dx) + (dy * dy));
                distance = Double.compare(distance, 0) == 0 ? 1 : distance;

                double angle         = adjustAngle(dx, dy, Math.abs(Math.toDegrees(Math.acos(dx / distance))));
                double radiusMinus05 = radius - 0.25;
                double radiusMinus10 = radius - 0.5;
                double radiusMinus15 = radius - 1.0;
                double radiusMinus20 = radius - 1.5;

                if (distance > radius) {
                    color = Color.TRANSPARENT;
                } else {
                    for (int i = 0; i < calculatedStopsLength; i++) {
                        if (angle >= (sortedStops.get(i).getOffset() * 360) && angle < (sortedStops.get(i + 1).getOffset() * 360)) {
                            double fraction = (angle - sortedStops.get(i).getOffset() * 360) / ((sortedStops.get(i + 1).getOffset() - sortedStops.get(i).getOffset()) * 360);
                            color = (Color) Interpolator.LINEAR.interpolate(sortedStops.get(i).getColor(), sortedStops.get(i + 1).getColor(), fraction);

                            if (distance > radiusMinus05) {
                                color = color.deriveColor(0.0, 1.0, 1.0, 0.25);
                            } else if (distance > radiusMinus10) {
                                color = color.deriveColor(0.0, 1.0, 1.0, 0.45);
                            } else if (distance > radiusMinus15) {
                                color = color.deriveColor(0.0, 1.0, 1.0, 0.65);
                            } else if (distance > radiusMinus20) {
                                color = color.deriveColor(0.0, 1.0, 1.0, 0.85);
                            }
                        }
                    }
                }
                PIXEL_WRITER.setColor(x, y, color);
            }
        }
        return roundRaster;
    }

    public ImagePattern apply(final Shape shape) {
        double x      = shape.getLayoutBounds().getMinX();
        double y      = shape.getLayoutBounds().getMinY();
        double width  = shape.getLayoutBounds().getWidth();
        double height = shape.getLayoutBounds().getHeight();
        centerX       = width * 0.5;
        centerY       = height * 0.5;
        return new ImagePattern(getImage(width, height), x, y, width, height, false);
    }

    public ImagePattern getImagePattern(final Bounds bounds) {
        return getImagePattern(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
    }
    public ImagePattern getImagePattern(final Rectangle bounds) {
        double x      = bounds.getX();
        double y      = bounds.getY();
        double width  = bounds.getWidth();
        double height = bounds.getHeight();
        centerX       = width * 0.5;
        centerY       = height * 0.5;
        return new ImagePattern(getImage(width, height), x, y, width, height, false);
    }
    public ImagePattern getImagePattern(final eu.hansolo.toolboxfx.geom.Bounds bounds) {
        double x      = bounds.getMinX();
        double y      = bounds.getMinY();
        double width  = bounds.getWidth();
        double height = bounds.getHeight();
        centerX       = bounds.getCenterX();
        centerY       = bounds.getCenterY();
        return new ImagePattern(getImage(width, height), x, y, width, height, false);
    }

    private double adjustAngle(final double dx, final double dy, double angle) {
        if (Double.compare(dx, 0) >= 0 && Double.compare(dy, 0) <= 0) {
            angle = 90.0 - angle;   // Upper Right Quadrant
        } else if (Double.compare(dx, 0) >= 0 && Double.compare(dy, 0) >= 0) {
            angle += 90.0;          // Lower Right Quadrant
        } else if (Double.compare(dx, 0) <= 0 && Double.compare(dy, 0) >= 0) {
            angle += 90.0;          // Lower Left Quadrant
        } else if (Double.compare(dx, 0) <= 0 && Double.compare(dy, 0) <= 0) {
            angle = 450.0 - angle;  // Upper Left Qudrant
        }

        /*
        if (DX >= 0 && DY <= 0) {
            angle = 90.0 - angle;   // Upper Right Quadrant
        } else if (DX >= 0 && DY >= 0) {
            angle += 90.0;          // Lower Right Quadrant
        } else if (DX <= 0 && DY >= 0) {
            angle += 90.0;          // Lower Left Quadrant
        } else if (DX <= 0 && DY <= 0) {
            angle = 450.0 - angle;  // Upper Left Qudrant
        }
        */
        return angle;
    }

    private List<Stop> calculate(final List<Stop> stops, final double offset) {
        List<Stop> stps = new ArrayList<>(stops.size());
        final BigDecimal STEP = BigDecimal.valueOf(Double.MIN_VALUE);
        for (Stop stop : stops) {
            double     offst       = stop.getOffset();
            Color      color       = stop.getColor();
            BigDecimal newOffsetBD = BigDecimal.valueOf(offst + offset).remainder(BigDecimal.ONE);
            if (newOffsetBD.equals(BigDecimal.ZERO)) {
                newOffsetBD = BigDecimal.ONE;
                stps.add(new Stop(Double.MIN_VALUE, color));
            } else if (Double.compare((offst + offset), 1.0) > 0) {
                newOffsetBD = newOffsetBD.subtract(STEP);
            }
            stps.add(new Stop(newOffsetBD.doubleValue(), color));
        }

        HashMap<Double, Color> stopMap = new LinkedHashMap<>(stps.size());
        for (Stop stop : stps) { stopMap.put(stop.getOffset(), stop.getColor()); }

        List<Stop>        sortedStops     = new ArrayList<>(stps.size());
        SortedSet<Double> sortedFractions = new TreeSet<>(stopMap.keySet());
        if (sortedFractions.last() < 1) {
            stopMap.put(1.0, stopMap.get(sortedFractions.first()));
            sortedFractions.add(1.0);
        }
        if (sortedFractions.first() > 0) {
            stopMap.put(0.0, stopMap.get(sortedFractions.last()));
            sortedFractions.add(0.0);
        }
        for (double fraction : sortedFractions) { sortedStops.add(new Stop(fraction, stopMap.get(fraction))); }

        return sortedStops;
    }

    /*
    private List<Stop> normalizeStops(final Stop... STOPS) { return normalizeStops(0, Arrays.asList(STOPS)); }
    private List<Stop> normalizeStops(final double OFFSET, final Stop... STOPS) { return normalizeStops(OFFSET, Arrays.asList(STOPS)); }
    private List<Stop> normalizeStops(final List<Stop> STOPS) { return normalizeStops(0, STOPS); }
    */
    private List<Stop> normalizeStops(final double offset, final List<Stop> stops) {
        double offst = Helper.clamp(0.0, 1.0, offset);
        List<Stop> stps;
        if (null == stops || stops.isEmpty()) {
            stps = new ArrayList<>();
            stps.add(new Stop(0.0, Color.TRANSPARENT));
            stps.add(new Stop(1.0, Color.TRANSPARENT));
        } else {
            stps = stops;
        }
        List<Stop> sortedStops = calculate(stps, offst);

        // Reverse the Stops for CCW direction
        if (ScaleDirection.COUNTER_CLOCKWISE == scaleDirection) {
            List<Stop> sortedStops3 = new ArrayList<>();
            Collections.reverse(sortedStops);
            for (Stop stop : sortedStops) { sortedStops3.add(new Stop(1.0 - stop.getOffset(), stop.getColor())); }
            sortedStops = sortedStops3;
        }
        return sortedStops;
    }
}

