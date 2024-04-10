package example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.java.fr.uga.pddl4j.encoding.CodedProblem;
import main.java.fr.uga.pddl4j.planners.hsp.AStarPlanner;
import main.java.fr.uga.pddl4j.util.BitOp;

/**
 * The RunPlanner class provides functionality to run a planner and extract plan steps.
 */
public class RunPlanner {
    // Define a lock object for synchronization
    private static final Object lock = new Object();
    
    /**
     * Runs a planner to generate a plan based on given beliefs and predicates.
     * 
     * @param agName The name of the agent.
     * @param beliefs The agent's beliefs.
     * @param predicate The predicates to achieve.
     * @param choiceOfPlanner The choice of planner (1 for FF planner, 2 for PDDL4J Implementation).
     * @return The generated plan as a list of steps.
     */
    public static List<String> run(String agName, List<String> beliefs, List<String> predicate, int choiceOfPlanner) {
        PDDLGenerator.generate(agName, beliefs, predicate); 
        List<String> plan = new ArrayList<>();
        if (choiceOfPlanner == 1) {
            try {
                // Command to execute
                String command = "./ff -o domain.pddl -f " + agName + "problem.pddl";
    
                // Create ProcessBuilder
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
    
                // Redirect error stream to output stream
                processBuilder.redirectErrorStream(true);
    
                // Start the process
                Process process = processBuilder.start();
                String output;
                try (// Read the output of the process
                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A")) {
                    output = scanner.hasNext() ? scanner.next() : "";}
    
                // Wait for the process to finish
                process.waitFor();
                
                //System.out.println(output);
                plan = extractSteps(output);
                //System.out.println(plan);
                return plan;
            } catch (IOException | InterruptedException e) {e.printStackTrace();}
            return null;
        }
        if (choiceOfPlanner == 2) {
            String args[] = new String[]{"-o", "domain.pddl", "-f", agName+"problem.pddl", "-u", "7"};
            
            // Synchronize access to ensure mutual exclusion
            synchronized (lock) {
                plan = runPlannerSafely(args);
                return plan;
            }
        }
        return null;
    }
    
    /**
     * Extracts plan steps from the output of the planner.
     * 
     * @param output The output of the planner.
     * @return The list of plan steps.
     */
    private static List<String> extractSteps(String output) {
        List<String> steps = new ArrayList<>();

        // Define a pattern to match lines starting with a number followed by a colon
        Pattern pattern = Pattern.compile("\\d+:\\s+([A-Z]+)");

        // Create a matcher with the input string
        Matcher matcher = pattern.matcher(output);

        // Find all matches
        while (matcher.find()) {
            String step = matcher.group(1);
            steps.add(step);
        }
        return steps;
    }

    /**
     * Method to run the planner safely within a synchronized block.
     * 
     * @param args The arguments for the planner.
     * @return A list of actions in the plan.
     */
    private synchronized static List<String> runPlannerSafely(String[] args) {
        List<String> plan = new ArrayList<>();
        try {
            // Parse planner arguments
            Properties arguments = AStarPlanner.parseArguments(args);
            // Instantiate A* planner
            AStarPlanner planner = new AStarPlanner(arguments);
            
            // Parse and encode the problem
            CodedProblem problem = planner.parseAndEncode();
            
            // Generate one-step plan
            BitOp action = planner.oneStepPlan(arguments);
            // Convert action to string representation
            String actionString = problem.toShortString(action);
            // Add action to the plan
            plan.add(actionString); 
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
        return plan;
    }
}
