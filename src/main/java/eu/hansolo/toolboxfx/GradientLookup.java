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
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class GradientLookup {
    private Map<Double, Stop> stops;


    // ******************** Constructors **************************************
    public GradientLookup () {
        this(new Stop[]{});
    }
    public GradientLookup(final Stop... stops) {
        this(Arrays.asList(stops));
    }
    public GradientLookup(final List<Stop> stops) {
        this.stops = new TreeMap<>();
        for (Stop stop : stops) { this.stops.put(stop.getOffset(), stop); }
        init();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (stops.isEmpty()) return;

        double minFraction = Collections.min(stops.keySet());
        double maxFraction = Collections.max(stops.keySet());

        if (Double.compare(minFraction, 0) > 0) { stops.put(0.0, new Stop(0.0, stops.get(minFraction).getColor())); }
        if (Double.compare(maxFraction, 1) < 0) { stops.put(1.0, new Stop(1.0, stops.get(maxFraction).getColor())); }
    }


    // ******************** Methods *******************************************
    public Color getColorAt(final double positionOfColor) {
        if (stops.isEmpty()) return Color.BLACK;
        final int    size     = stops.size();
        final double position = Helper.clamp(0.0, 1.0, positionOfColor);
        final Color  color;
        if (size == 1) {
            final Map<Double, Color> oneEntry = (Map<Double, Color>) stops.entrySet().iterator().next();
            color = stops.get(oneEntry.keySet().iterator().next()).getColor();
        } else {
            Stop lowerBound = stops.get(0.0);
            Stop upperBound = stops.get(1.0);
            int  counter    = 0;
            for (Entry<Double, Stop> entry : stops.entrySet()) {
                final double fraction = entry.getKey();
                final Stop   stop     = entry.getValue();
                if (counter != size - 1 && Double.compare(fraction, position) == 0) {
                    lowerBound = stop;
                } else if (Double.compare(fraction, position) < 0) {
                    lowerBound = stop;
                } else if (Double.compare(fraction, position) > 0) {
                    upperBound = stop;
                    break;
                }
                counter++;
            }
            color = interpolateColor(lowerBound, upperBound, position);
        }
        return color;
    }

    public List<Stop> getStops() { return new ArrayList<>(stops.values()); }
    public void setStops(final Stop... stops) { setStops(Arrays.asList(stops)); }
    public void setStops(final List<Stop> stops) {
        this.stops.clear();
        for (Stop stop : stops) { this.stops.put(stop.getOffset(), stop); }
        init();
    }

    public Stop getStopAt(final double positionOfStop) {
        if (stops.isEmpty()) { throw new IllegalArgumentException("GradientStop stops should not be empty"); };

        final double position = Helper.clamp(0.0, 1.0, positionOfStop);

        Stop stop = null;
        double distance = Math.abs(stops.get(Double.valueOf(0)).getOffset() - position);
        for(Entry<Double, Stop> entry : stops.entrySet()) {
            double cdistance = Math.abs(entry.getKey() - position);
            if (cdistance < distance) {
                stop = stops.get(entry.getKey());
                distance = cdistance;
            }
        }
        return stop;
    }

    public List<Stop> getStopsBetween(final double minOffset, final double maxOffset) {
        List<Stop> selectedStops = new ArrayList<>();
        for (Entry<Double, Stop> entry : stops.entrySet()) {
            if (entry.getValue().getOffset() >= minOffset && entry.getValue().getOffset() <= maxOffset) { selectedStops.add(entry.getValue()); }
        }
        return selectedStops;
    }

    private Color interpolateColor(final Stop lowerBound, final Stop upperBound, final double position) {
        final double pos  = (position - lowerBound.getOffset()) / (upperBound.getOffset() - lowerBound.getOffset());

        final double deltaRed     = (upperBound.getColor().getRed()     - lowerBound.getColor().getRed())     * pos;
        final double deltaGreen   = (upperBound.getColor().getGreen()   - lowerBound.getColor().getGreen())   * pos;
        final double deltaBlue    = (upperBound.getColor().getBlue()    - lowerBound.getColor().getBlue())    * pos;
        final double deltaOpacity = (upperBound.getColor().getOpacity() - lowerBound.getColor().getOpacity()) * pos;

        final double red     = Helper.clamp(0.0, 1.0, (lowerBound.getColor().getRed() + deltaRed));
        final double green   = Helper.clamp(0.0, 1.0, (lowerBound.getColor().getGreen()   + deltaGreen));
        final double blue    = Helper.clamp(0.0, 1.0, (lowerBound.getColor().getBlue()    + deltaBlue));
        final double opacity = Helper.clamp(0.0, 1.0, (lowerBound.getColor().getOpacity() + deltaOpacity));

        return Color.color(red, green, blue, opacity);
    }
}
