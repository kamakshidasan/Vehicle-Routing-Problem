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

package org.optaplanner.examples.common.business;

import java.io.File;
import javax.swing.SwingUtilities;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.event.BestSolutionChangedEvent;
import org.optaplanner.core.api.solver.event.SolverEventListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolverAndPersistenceFrame;

public class SolutionBusiness {

    private SolutionDao solutionDao;

    // volatile because the solve method doesn't come from the event thread (like every other method call)
    private volatile Solver solver;
    private String solutionFileName = null;
    private ScoreDirector guiScoreDirector;

    public void setSolutionDao(SolutionDao solutionDao) {
        this.solutionDao = solutionDao;
    }

    public void setSolver(Solver solver) {
        this.solver = solver;
        ScoreDirectorFactory scoreDirectorFactory = solver.getScoreDirectorFactory();
        guiScoreDirector = scoreDirectorFactory.buildScoreDirector();
    }

    public Solution getSolution() {
        return guiScoreDirector.getWorkingSolution();
    }

    public void setSolution(Solution solution) {
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void registerForBestSolutionChanges(final SolverAndPersistenceFrame solverAndPersistenceFrame) {
        solver.addEventListener(new SolverEventListener<Solution>() {
            // Not called on the event thread
            public void bestSolutionChanged(BestSolutionChangedEvent<Solution> event) {
                // Avoid ConcurrentModificationException when there is an unprocessed ProblemFactChange
                // because the paint method uses the same problem facts instances as the Solver's workingSolution
                // unlike the planning entities of the bestSolution which are cloned from the Solver's workingSolution
                if (solver.isEveryProblemFactChangeProcessed()) {
                    // final is also needed for thread visibility
                    final Solution latestBestSolution = event.getNewBestSolution();
                    // Migrate it to the event thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // TODO by the time we process this event, a newer bestSolution might already be queued
                            guiScoreDirector.setWorkingSolution(latestBestSolution);

                        }
                    });
                }
            }
        });
    }

    public void openSolution(File file) {
        Solution solution = solutionDao.readSolution(file);
        solutionFileName = file.getName();
        guiScoreDirector.setWorkingSolution(solution);
    }

    public void saveSolution(File file) {
        Solution solution = guiScoreDirector.getWorkingSolution();
        solutionDao.writeSolution(solution, file);
    }

    /**
     * Can be called on any thread.
     * <p/>
     * Note: This method does not change the guiScoreDirector because that can only be changed on the event thread.
     * @param planningProblem never null
     * @return never null
     */
    public Solution solve(Solution planningProblem) {
        solver.solve(planningProblem);
        return solver.getBestSolution();
    }
}
