/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.examples.common.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;

import java.io.File;

public abstract class CommonApp extends LoggingMain {

    protected final String name;

    public File inputFile;

    protected SolverAndPersistenceFrame solverAndPersistenceFrame;
    protected SolutionBusiness solutionBusiness;

    protected CommonApp(String name) {
        this.name = name;
    }


    public File init(File inputFile) {
        this.inputFile=inputFile;
        return init();
    }

    public File init() {
        solutionBusiness = createSolutionBusiness();
        solverAndPersistenceFrame = new SolverAndPersistenceFrame(solutionBusiness, createSolutionPanel());
        solverAndPersistenceFrame.getInputFile(inputFile);
        solverAndPersistenceFrame.openFile();
        solverAndPersistenceFrame.solveFile();
        solverAndPersistenceFrame.saveFile();
        return solverAndPersistenceFrame.getOutputFile();
    }

    public SolutionBusiness createSolutionBusiness() {
        SolutionBusiness solutionBusiness = new SolutionBusiness();
        solutionBusiness.setSolutionDao(createSolutionDao());
        solutionBusiness.setSolver(createSolver());
        return solutionBusiness;
    }

    protected abstract Solver createSolver();

    protected abstract SolutionPanel createSolutionPanel();

    protected abstract SolutionDao createSolutionDao();

}
