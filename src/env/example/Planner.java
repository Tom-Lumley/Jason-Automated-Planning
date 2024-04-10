package example;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jason.asSemantics.Agent;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

/**
 * The Planner class represents an internal action for planning actions.
 */
public class Planner extends DefaultInternalAction {
    // Loggers for clean outputs
    private Logger recoveryLogger = Logger.getLogger("t1."+"Recovery");


    /**
     * Executes the internal action for planning actions.
     * 
     * @param ts The transition system.
     * @param un The unifier.
     * @param args The arguments for planning.
     * @return True if the planning is successful, false otherwise.
     * @throws Exception If an exception occurs during execution.
     */
    public Object execute(TransitionSystem ts,Unifier un,Term[] args)throws Exception {
        // Obtain Key Information
        Agent ag = ts.getAg();
        String agName =ts.getAgArch().getAgName();
        

        // Initialisation
        List<String> predicates = new ArrayList<>();
        Action action = new Action();  
        int typeOfPlanning = action.typeOfPlanning;
        boolean success = false;

        // Extract Beliefs, See which predicates are missing from bb..
        List<String> beliefs = extractBeliefs(ag);
        for (Term pred : args) {predicates.add(pred.toString());}
        List<String> goalStates = findPredicates(beliefs, predicates);

        // If Type of planning selected is invalid... Default to Online
        if (typeOfPlanning != 1 && typeOfPlanning != 2) {
            recoveryLogger.info("Invalid Type of Planning Selected... Defaulting to Online Planning");
            typeOfPlanning = 2;
        }
        if (typeOfPlanning==1) {
            List<String> plan = RunPlanner.run(agName, beliefs, goalStates, 1);

            if(plan.isEmpty()){
                System.out.println("An error occured with the planner");
                System.out.println("To debug: Go to RunPlanner.java and print the output.");
                return false;
            }

            // Log recovery and planner information
            recoveryLogger.info(agName+" --> Direct Planning Call --> Running Action --> "+plan.get(0).toString());

            for (String act : plan) {
                success = action.startAction(ag, act.toLowerCase()); 
                if (success) {return true;} else {return false;}
            }
        }
        if (typeOfPlanning ==2) {
            // While goalStates are not fulfilled...
            while (goalStates.size() != 0) {
                // Prepare a list for the planner with the provided argument
                List<String> plan = RunPlanner.run(agName, beliefs, goalStates, 2);
                
                // Debugging information if plan produced by planner is empty...
                if(plan.isEmpty()){
                    System.out.println("An error occured with the planner");
                    System.out.println("To debug: Go to RunPlanner.java and print the output.");
                    return false;
                }   

                // Execute the actions returned by the planner
                success = action.startAction(ag, plan.get(0).toLowerCase());
                if (!success) {recoveryLogger.info("Recovery failure");} 

                beliefs = extractBeliefs(ag);
                goalStates = findPredicates(beliefs, predicates); 
            }
        }

        if (success) {return true;} else {return false;}
    }

    /**
     * Finds the predicates that are missing in the agent's beliefs.
     * 
     * @param beliefs The agent's beliefs.
     * @param predicates The list of predicates to check.
     * @return The list of missing predicates.
     */
    private static List<String> findPredicates(List<String> beliefs, List<String> predicates) {
        List<String> missingPredicates = new ArrayList<>();
        for (String pred : predicates) {
            if (!beliefs.contains(pred)) {
                missingPredicates.add(pred);
            }
        }
        return missingPredicates;
    }

    /**
     * Extracts beliefs from the agent's belief base, filtering out source annotations.
     * 
     * @param ag The agent.
     * @return The list of extracted beliefs.
     */
    public List<String> extractBeliefs(Agent ag) {
        List<String> filteredBeliefs = new ArrayList<>();
        for (Literal b : ag.getBB()) {
            String beliefString = b.toString();
            if (!beliefString.startsWith("kqml")) {
                beliefString = beliefString.replace("[source(self)]", "").replace("[source(percepts)]", "");
                filteredBeliefs.add(beliefString.trim());
            }
        }
        return filteredBeliefs;
    }
}
