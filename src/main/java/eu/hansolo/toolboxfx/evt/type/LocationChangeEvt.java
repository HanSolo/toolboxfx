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

package eu.hansolo.toolboxfx.evt.type;

import eu.hansolo.toolbox.evt.EvtPriority;
import eu.hansolo.toolbox.evt.EvtType;
import eu.hansolo.toolbox.evt.type.ChangeEvt;
import eu.hansolo.toolboxfx.geom.Location;


public class LocationChangeEvt extends ChangeEvt {
    public static final EvtType<LocationChangeEvt> ANY                = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<LocationChangeEvt> TIMESTAMP_CHANGED  = new EvtType<>(LocationChangeEvt.ANY, "TIMESTAMP_CHANGED");
    public static final EvtType<LocationChangeEvt> NAME_CHANGED       = new EvtType<>(LocationChangeEvt.ANY, "NAME_CHANGED");
    public static final EvtType<LocationChangeEvt> INFO_CHANGED       = new EvtType<>(LocationChangeEvt.ANY, "INFO_CHANGED");
    public static final EvtType<LocationChangeEvt> LOCATION_CHANGED   = new EvtType<>(LocationChangeEvt.ANY, "LOCATION_CHANGED");
    public static final EvtType<LocationChangeEvt> ALTITUDE_CHANGED   = new EvtType<>(LocationChangeEvt.ANY, "ALTITUDE_CHANGED");
    public static final EvtType<LocationChangeEvt> ACCURACY_CHANGED   = new EvtType<>(LocationChangeEvt.ANY, "ACCURACY_CHANGED");
    public static final EvtType<LocationChangeEvt> FILL_CHANGED       = new EvtType<>(LocationChangeEvt.ANY, "FILL_CHANGED");
    public static final EvtType<LocationChangeEvt> STROKE_CHANGED     = new EvtType<>(LocationChangeEvt.ANY, "STROKE_CHANGED");
    public static final EvtType<LocationChangeEvt> ZOOM_LEVEL_CHANGED = new EvtType<>(LocationChangeEvt.ANY, "ZOOM_LEVEL_CHANGED");

    private final Location oldLocation;
    private final Location location;


    // ******************** Constructors **************************************
    public LocationChangeEvt(final EvtType<? extends LocationChangeEvt> evtType, final Location oldLocation, final Location location) {
        super(evtType);
        this.location    = location;
        this.oldLocation = oldLocation;
    }
    public LocationChangeEvt(final Object src, final EvtType<? extends LocationChangeEvt> evtType, final Location oldLocation, final Location location) {
        super(src, evtType);
        this.location    = location;
        this.oldLocation = oldLocation;
    }
    public LocationChangeEvt(final Object src, final EvtType<? extends LocationChangeEvt> evtType, final EvtPriority priority, final Location oldLocation, final Location location) {
        super(src, evtType, priority);
        this.location    = location;
        this.oldLocation = oldLocation;
    }


    // ******************** Methods *******************************************
    public EvtType<? extends LocationChangeEvt> getEvtType() { return (EvtType<? extends LocationChangeEvt>) super.getEvtType(); }

    public Location getOldLocation() { return oldLocation; }

    public Location getLocation() { return location; }
}
