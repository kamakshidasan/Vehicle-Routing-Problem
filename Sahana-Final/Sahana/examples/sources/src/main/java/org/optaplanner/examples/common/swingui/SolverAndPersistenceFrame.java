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

package org.optaplanner.examples.common.swingui;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.business.SolutionBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.ExecutionException;

public class SolverAndPersistenceFrame{

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private File inputFile, outputFile;

    private final SolutionBusiness solutionBusiness;

    private SolutionPanel solutionPanel;

    public SolverAndPersistenceFrame(SolutionBusiness solutionBusiness,
                                     SolutionPanel solutionPanel) {
        this.solutionBusiness = solutionBusiness;
        this.solutionPanel = solutionPanel;
        solutionPanel.setSolutionBusiness(solutionBusiness);
        solutionPanel.setSolverAndPersistenceFrame(this);
        registerListeners();
    }

    private void registerListeners() {
        solutionBusiness.registerForBestSolutionChanges(this);
    }

    protected class SolveWorker extends SwingWorker<Solution, Void> {

        protected final Solution planningProblem;

        public SolveWorker(Solution planningProblem) {
            this.planningProblem = planningProblem;
        }

        @Override
        protected Solution doInBackground() throws Exception {
            return solutionBusiness.solve(planningProblem);
        }

        @Override
        protected void done() {
            try {
                Solution bestSolution = get();
                solutionBusiness.setSolution(bestSolution);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Solving interrupted.", e);
            } catch (ExecutionException e) {
                throw new IllegalStateException("Solving failed.", e.getCause());
            } finally {
                setSolvingState(false);
                resetScreen();
            }
        }

    }

    private void setSolutionLoaded() {
        resetScreen();
    }

    private void setSolvingState(boolean solving) {
        solutionPanel.setSolvingState(solving);
    }

    public void resetScreen() {
        solutionPanel.resetPanel(solutionBusiness.getSolution());
    }

    public void getInputFile(File inputFile){
        this.inputFile=inputFile;
    }

    public File getOutputFile(){
        return outputFile;
    }

    public void openFile(){
        logger.info("Opening: {}", inputFile.getAbsolutePath());
        new OpenAction(inputFile);
    }

    public void solveFile(){
        logger.info("Solving: {}", inputFile.getAbsolutePath());
        new SolveAction();
    }

    public void saveFile(){
        logger.info("Saving: {}", inputFile.getAbsolutePath());
        new SaveAction();
    }


    private class OpenAction{
        public OpenAction(File inputFile) {
            try {
                solutionBusiness.openSolution(inputFile);
                setSolutionLoaded();
            } catch(Exception exception) {
                logger.info("Exception: {}", exception.getMessage());
            }
        }
    }

    private class SolveAction{
        public SolveAction() {
            setSolvingState(true);
            Solution planningProblem = solutionBusiness.getSolution();
            SolveWorker solveWorker=new SolveWorker(planningProblem);
            solveWorker.execute();
            solveWorker.done();
        }
    }

    private class SaveAction{
        public SaveAction() {
            try {
                String outputFileName = "solved-"+inputFile.getName();
                outputFile=new File(inputFile.getParent(),outputFileName);
                solutionBusiness.saveSolution(outputFile);
            } catch(Exception exception) {
                logger.info("Exception: {}", exception.getMessage());
            }
        }
    }
}