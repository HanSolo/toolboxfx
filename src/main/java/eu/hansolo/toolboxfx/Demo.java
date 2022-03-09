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

import eu.hansolo.toolboxfx.evt.type.LocationChangeEvt;
import eu.hansolo.toolboxfx.geom.Bounds;
import eu.hansolo.toolboxfx.geom.Location;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;


public class Demo {

    public Demo() {
        Bounds bounds = new Bounds();
        locationDemo();
    }


    private void locationDemo() {
        Location home = new Location(7.38, 51.51);
        home.addLocationObserver(LocationChangeEvt.LOCATION_CHANGED, e -> System.out.println("Location observer: " + e.getOldLocation() + "\n---------\n" + e.getLocation()));
        home.addLocationObserver(LocationChangeEvt.ALTITUDE_CHANGED, e -> System.out.println("Altitude observer: " + e.getLocation().getAltitude()));
        home.setLatitude(8.0);
    }

    public static void main(String[] args) {
        new Demo();
    }
}
