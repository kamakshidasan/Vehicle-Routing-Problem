/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.vehiclerouting.swingui;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.vehiclerouting.domain.Location;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;

public class VehicleRoutingPanel extends SolutionPanel {

    private Long nextLocationId = null;

    public VehicleRoutingPanel() {

    }

    public VehicleRoutingSolution getSchedule() {
        return (VehicleRoutingSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        resetNextLocationId();
    }

    private void resetNextLocationId() {
        long highestLocationId = 0L;
        for (Location location : getSchedule().getLocationList()) {
            if (highestLocationId < location.getId().longValue()) {
                highestLocationId = location.getId();
            }
        }
        nextLocationId = highestLocationId + 1L;
    }
}