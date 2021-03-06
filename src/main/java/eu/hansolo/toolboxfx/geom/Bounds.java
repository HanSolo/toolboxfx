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
import eu.hansolo.toolboxfx.evt.type.BoundsEvt;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static eu.hansolo.toolbox.Constants.COLON;
import static eu.hansolo.toolbox.Constants.COMMA;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_CLOSE;
import static eu.hansolo.toolbox.Constants.CURLY_BRACKET_OPEN;
import static eu.hansolo.toolbox.Constants.QUOTES;


public class Bounds {
    private double                                     x;
    private double                                     y;
    private double                                     width;
    private double                                     height;
    private Map<EvtType, List<EvtObserver<BoundsEvt>>> observers;



    // ******************** Constructors **************************************
    public Bounds() {
        this(0, 0, 0, 0);
    }
    public Bounds(final double width, final double height) {
        this(0, 0, width, height);
    }
    public Bounds(final double x, final double y, final double width, final double height) {
        observers = new ConcurrentHashMap<>();
        set(x, y, width, height);
    }


    // ******************** Methods *******************************************
    public double getX() { return x; }
    public void setX(final double x) {
        this.x = x;
        fireBoundsEvt(new BoundsEvt(Bounds.this, BoundsEvt.BOUNDS, Bounds.this));
    }

    public double getY() { return y; }
    public void setY(final double y) {
        this.y = y;
        fireBoundsEvt(new BoundsEvt(Bounds.this, BoundsEvt.BOUNDS, Bounds.this));
    }

    public double getMinX() { return x; }
    public double getMaxX() { return x + width; }

    public double getMinY() { return y; }
    public double getMaxY() { return y + height; }

    public double getWidth() { return width; }
    public void setWidth(final double width) {
        this.width = Helper.clamp(0, Double.MAX_VALUE, width);
        fireBoundsEvt(new BoundsEvt(Bounds.this, BoundsEvt.BOUNDS, Bounds.this));
    }

    public double getHeight() { return height; }
    public void setHeight(final double height) {
        this.height = Helper.clamp(0, Double.MAX_VALUE, height);
        fireBoundsEvt(new BoundsEvt(Bounds.this, BoundsEvt.BOUNDS, Bounds.this));
    }

    public double getCenterX() { return x + width * 0.5; }
    public double getCenterY() { return y + height * 0.5; }

    public void set(final Bounds bounds) {
        set(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }
    public void set(final double x, final double y, final double width, final double height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        fireBoundsEvt(new BoundsEvt(Bounds.this, BoundsEvt.BOUNDS, Bounds.this));
    }

    public boolean contains(final double x, final double y) {
        return (Double.compare(x, getMinX()) >= 0 && Double.compare(x, getMaxX()) <= 0 &&
                Double.compare(y, getMinY()) >= 0 && Double.compare(y, getMaxY()) <= 0);
    }

    public boolean intersects(final Bounds other) {
        return (other.getMaxX() >= getMinX() && other.getMaxY() >= getMinY() &&
                other.getMinX() <= getMaxX() && other.getMinY() <= getMaxY());
    }
    public boolean intersects(final double x, final double y, final double width, final double height) {
        return (x + width >= getMinX() && y + height >= getMinY() &&
                x <= getMaxX() && y <= getMaxY());
    }

    public Bounds copy() { return new Bounds(x, y, width, height); }


    // ******************** Event handling ************************************
    public void addBoundsObserver(final EvtType<? extends Evt> type, final EvtObserver<BoundsEvt> observer) {
        if (!observers.containsKey(type)) { observers.put(type, new CopyOnWriteArrayList<>()); }
        if (observers.get(type).contains(observer)) { return; }
        observers.get(type).add(observer);
    }
    public void removeBoundsObserver(final EvtType<? extends Evt> type, final EvtObserver<BoundsEvt> observer) {
        if (observers.containsKey(type) && observers.get(type).contains(observer)) {
            observers.get(type).remove(observer);
        }
    }
    public void removeAllBoundsObservers() { observers.clear(); }

    public void fireBoundsEvt(final BoundsEvt evt) {
        final EvtType type = evt.getEvtType();
        observers.entrySet().stream().filter(entry -> entry.getKey().equals(BoundsEvt.ANY)).forEach(entry -> entry.getValue().forEach(observer -> observer.handle(evt)));
        if (observers.containsKey(type)) {
            observers.get(type).forEach(observer -> observer.handle(evt));
        }
    }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj instanceof Bounds) {
            Bounds other = (Bounds) obj;
            return getX() == other.getX() &&
                   getY() == other.getY() &&
                   getWidth() == other.getWidth() &&
                   getHeight() == other.getHeight();
        } else return false;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @Override public String toString() {
        return new StringBuilder().append(CURLY_BRACKET_OPEN)
                                  .append(QUOTES).append("x").append(QUOTES).append(COLON).append(getX()).append(COMMA)
                                  .append(QUOTES).append("y").append(QUOTES).append(COLON).append(getY()).append(COMMA)
                                  .append(QUOTES).append("w").append(QUOTES).append(COLON).append(getWidth()).append(COMMA)
                                  .append(QUOTES).append("h").append(QUOTES).append(COLON).append(getHeight())
                                  .append(CURLY_BRACKET_CLOSE)
                                  .toString();
    }
}
