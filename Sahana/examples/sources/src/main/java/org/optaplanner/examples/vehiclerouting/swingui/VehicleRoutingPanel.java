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
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Location;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedDepot;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedVehicleRoutingSolution;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class VehicleRoutingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/vehiclerouting/swingui/vehicleRoutingLogo.png";

    private Random demandRandom = new Random(37);
    private Long nextLocationId = null;

    public VehicleRoutingPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public VehicleRoutingSolution getSchedule() {
        return (VehicleRoutingSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        VehicleRoutingSolution schedule = (VehicleRoutingSolution) solution;
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

    @Override
    public void updatePanel(Solution solution) {
        VehicleRoutingSolution schedule = (VehicleRoutingSolution) solution;
    }

    public void doMove(Move move) {
        solutionBusiness.doMove(move);
    }

    public SolverAndPersistenceFrame getWorkflowFrame() {
        return solverAndPersistenceFrame;
    }

    public void insertLocationAndCustomer(double longitude, double latitude) {
        final Location newLocation = new Location();
        newLocation.setId(nextLocationId);
        nextLocationId++;
        newLocation.setLongitude(longitude);
        newLocation.setLatitude(latitude);
        logger.info("Scheduling insertion of newLocation ({}).", newLocation);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                VehicleRoutingSolution schedule = (VehicleRoutingSolution) scoreDirector.getWorkingSolution();
                scoreDirector.beforeProblemFactAdded(newLocation);
                schedule.getLocationList().add(newLocation);
                scoreDirector.afterProblemFactAdded(newLocation);
                Customer newCustomer;
                if (schedule instanceof TimeWindowedVehicleRoutingSolution) {
                    TimeWindowedCustomer newTimeWindowedCustomer = new TimeWindowedCustomer();
                    TimeWindowedDepot timeWindowedDepot = (TimeWindowedDepot) schedule.getDepotList().get(0);
                    int windowTime = (timeWindowedDepot.getDueTime() - timeWindowedDepot.getReadyTime()) / 4;
                    int readyTime = demandRandom.nextInt(windowTime * 3);
                    newTimeWindowedCustomer.setReadyTime(readyTime);
                    newTimeWindowedCustomer.setDueTime(readyTime + windowTime);
                    newTimeWindowedCustomer.setServiceDuration(Math.min(10000, windowTime / 2));
                    newCustomer = newTimeWindowedCustomer;
                } else {
                    newCustomer = new Customer();
                }
                newCustomer.setId(newLocation.getId());
                newCustomer.setLocation(newLocation);
                // Demand must not be 0
                newCustomer.setDemand(demandRandom.nextInt(10) + 1);
                scoreDirector.beforeEntityAdded(newCustomer);
                schedule.getCustomerList().add(newCustomer);
                scoreDirector.afterEntityAdded(newCustomer);
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

}
