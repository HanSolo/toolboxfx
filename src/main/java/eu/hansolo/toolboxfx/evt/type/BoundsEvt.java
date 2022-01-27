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
import eu.hansolo.toolboxfx.geom.Bounds;

import java.util.Objects;


public class BoundsEvt extends ChangeEvt {
    public static final EvtType<BoundsEvt> ANY    = new EvtType<>(ChangeEvt.ANY, "ANY");
    public static final EvtType<BoundsEvt> BOUNDS = new EvtType<>(BoundsEvt.ANY, "BOUNDS");

    private final Bounds bounds;


    // ******************** Constructors **************************************
    public BoundsEvt(final EvtType<? extends BoundsEvt> evtType, final Bounds bounds) {
        super(evtType);
        this.bounds = bounds;
    }
    public BoundsEvt(final Object src, final EvtType<? extends BoundsEvt> evtType, final Bounds bounds) {
        super(src, evtType);
        this.bounds = bounds;
    }
    public BoundsEvt(final Object src, final EvtType<? extends BoundsEvt> evtType, final EvtPriority priority, final Bounds bounds) {
        super(src, evtType, priority);
        this.bounds = bounds;
    }


    // ******************** Methods *******************************************
    // ******************** Methods *******************************************
    @Override public EvtType<? extends BoundsEvt> getEvtType() { return (EvtType<? extends BoundsEvt>) super.getEvtType(); }

    public Bounds getBounds() { return bounds; }

    @Override public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        if (!super.equals(o)) { return false; }
        BoundsEvt boundsEvt = (BoundsEvt) o;
        return Objects.equals(bounds, boundsEvt.bounds);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), bounds);
    }
}
