package example;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;


/**
 * Represents selection, execution and recovering an action performed by an agent.
 */
public class Action {
    // Initialise Key Variables
    BeliefBase bb;
    boolean recoveryRequired;
    List<Literal> predicate;
    List<Literal> beliefsToAdd;
    List<Literal> beliefsToDelete;

    /**
     * Choice of Planner: Offline (1) or Online (2)
     */
    public int typeOfPlanning = 2;

    // Logger for normal logging
    private Logger logger = Logger.getLogger("t1."+Env.class.getName());
    // Logger for recovery logging
    private Logger recoveryLogger = Logger.getLogger("t1."+"Recovery");

   /**
     * Selects and executes the corresponding action.
     * @param ag The agent performing the action.
     * @param action The name of the action to execute.
     * @return True if the action was executed successfully, otherwise false.
     */
    public boolean startAction(Agent ag, String action) {
        logger.info(getAgName(ag)+" executing: " + action); 
        
        predicate = new ArrayList<>();
        beliefsToAdd = new ArrayList<>();
        beliefsToDelete = new ArrayList<>();
        
        if (action.equals("buyphone")) {
            boolean success = buyPhone(ag, action);
            if(success) {return true;} else {return false;}
        }

        if (action.equals("dochores")) {
            boolean success = doChores(ag, action);
            if(success) {return true;} else {return false;}
        }

        if (action.equals("earnsalary")) {
            boolean success = earnSalary(ag, action);
            if(success) {return true;} else {return false;}
        }

        if (action.equals("usephone")) {
            boolean success = usePhone(ag, action);
            if(success) {return true;} else {return false;}
        }   

        if (action.equals("textfriend")) {
            boolean success = textFriend(ag, action);
            if(success) {return true;} else {return false;}
        }    

        if (action.equals("gooffphone")) {
            boolean success = goOffPhone(ag, action);
            if(success) {return true;} else {return false;}
        }     

        if (action.equals("gotogym")) {
            boolean success = goToGym(ag, action);
            if(success) {return true;} else {return false;}
        }     

        if (action.equals("getincar")) {
            boolean success = getInCar(ag, action);
            if(success) {return true;} else {return false;}
        }     

        if (action.equals("gotowork")) {
            boolean success = goToWork(ag, action);
            if(success) {return true;} else {return false;}
        }     

        logger.info(action+" not Implemented....");
        return false;    
    }

    /**
     * Runs the selected action and handles predicate checks, belief additions, deletions, and recovery operations.
     * @param ag The agent performing the action.
     * @param action The name of the action to execute.
     * @param predicate The predicate associated with the action.
     * @param beliefsToAdd The beliefs to add after executing the action.
     * @param beliefsToDelete The beliefs to delete after executing the action.
     * @return True if the action was executed successfully, otherwise false.
     */
    public Boolean runAction(Agent ag, String action, List<Literal> predicate, List<Literal> beliefsToAdd, List<Literal> beliefsToDelete) { // Could make the literals lists
        bb = ag.getBB(); 
        recoveryRequired = false;

        // No Predicate... add/del beliefs
        if (predicate == null) {
            try {
                if(beliefsToAdd != null) {for (Literal belief : beliefsToAdd) {ag.addBel(belief);}}
                if(beliefsToDelete != null) {for (Literal belief : beliefsToDelete) {ag.delBel(belief);}}
                return true; // Ran action successfully
            } catch (Exception e) {e.printStackTrace();}    
        }

        // Checks if all predicates are in ag beliefbase
        boolean allBelsPresent = false;
        allBelsPresent = allBeliefsPresent(bb, predicate);

        // If all predicates in bb add/del bels...
        if (allBelsPresent) { 
            try {
                if(beliefsToAdd != null) {for (Literal belief : beliefsToAdd) {ag.addBel(belief);}}
                if(beliefsToDelete != null) {for (Literal belief : beliefsToDelete) {ag.delBel(belief);}}
                return true; // Ran action successfully
            } catch (Exception e) {e.printStackTrace();}         
        } else { 
            recoveryRequired = true; // if all predicates not present... recovery required...
        }

        if(recoveryRequired) { // Extract Knowledge for Failure Recovery...
            boolean success = recoveryOperation(ag, action, predicate); // Change type of Planning to swap between OFFLINE (1) and ONLINE (2)
            if(success) {return true;} else {return false;}
        }

        return false; // failed to runAction successfully
    }

    /**
     * Performs a recovery operation for the agent based on the given action and predicates.
     * 
     * @param ag The agent for which the recovery operation is performed.
     * @param action The action to be executed during the recovery operation.
     * @param predicates The list of predicates representing the desired state to recover.
     * @return True if the recovery operation succeeds, otherwise false.
     */
    private boolean recoveryOperation(Agent ag, String action, List<Literal> predicates) {

        // Extract Beliefs, Find which predicates are not in bb...
        List<String> beliefs = extractBeliefs(ag);
        List<String> goalStates = findPredicates(beliefs, predicates); 

        // If Type of planning selected is invalid... Default to Online
        if (typeOfPlanning != 1 && typeOfPlanning != 2) {
            recoveryLogger.info("Invalid Type of Planning Selected... Defaulting to Online Planning");
            typeOfPlanning = 2;
        }

        // Offline Planning
        if (typeOfPlanning == 1) {
            List<String> plan = RunPlanner.run(getAgName(ag), beliefs, goalStates, 1); 
            if(plan.isEmpty()){
                System.out.println("An error occured with the planner");
                System.out.println("To debug: Go to RunPlanner.java and print the output.");
                return false;
            }
            recoveryLogger.info(getAgName(ag)+" --> Action Predicate Failure --> "+plan);
            for (String act : plan) {
                boolean success = startAction(ag, act.toLowerCase()); 
                if (!success) {recoveryLogger.info("Recovery failure");}
            }
        }

        // Online Planning
        if (typeOfPlanning==2) {
            while (goalStates.size() != 0) {
                List<String> plan = RunPlanner.run(getAgName(ag), beliefs, goalStates, 2); 
                if(plan.isEmpty()){
                    System.out.println("An error occured with the planner");
                    System.out.println("To debug: Go to RunPlanner.java and print the output.");
                    return false;
                }
                recoveryLogger.info(getAgName(ag)+" --> Action Predicate Failure --> Running Action --> "+plan.get(0).toString() );
                
                boolean success = startAction(ag, plan.get(0).toLowerCase()); //execute action .get(0) as simulating online using FF
                if (!success) {recoveryLogger.info("Recovery failure");}
                beliefs = extractBeliefs(ag); //Check env again
                goalStates = findPredicates(beliefs, predicates); //Recheck
            }
        }

        // Execute Original Action
        boolean success = startAction(ag, action.toLowerCase());
        if (success) {return true;}else {return false;}
    }

    /**
     * Checks which predicates are present in the belief base.
     * @param beliefs The list of beliefs.
     * @param predicates The list of predicates to check.
     * @return The list of predicates that are missing from the belief base.
     */
    private static List<String> findPredicates(List<String> beliefs, List<Literal> predicates) {
        List<String> missingPredicates = new ArrayList<>();
        for (Literal pred : predicates) {
            if (!beliefs.contains(pred.toString())) {
                missingPredicates.add(pred.toString());
            }
        }
        return missingPredicates;
    }

    /**
     * Extracts beliefs from the agent's belief base, filtering out KQML beliefs and irrelevant sources.
     * @param ag The agent.
     * @return The list of filtered beliefs.
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
    
    /**
     * Checks if all predicates are present in the belief base.
     * @param beliefBase The belief base to check.
     * @param predicate The list of predicates to check.
     * @return True if all predicates are present, otherwise false.
     */
    private boolean allBeliefsPresent(BeliefBase beliefBase, List<Literal> predicate) {
        for (Literal belief : predicate) {
            if (beliefBase.contains(belief) == null) {
                return false; // If any belief is not present, return false immediately
            }
        }
        return true; // If all beliefs are present, return true
    }
 
    /**
     * Gets the name of the agent.
     * @param ag The agent.
     * @return The name of the agent.
     */
    private String getAgName(Agent ag) {
        return ag.getTS().getAgArch().getAgName();
    }

    /* When creating your customActionFunction(Agent ag, String action) -- Must these contain arguments at minimum
     * To add predicates use: predicate.add(Literal)
     * To add beliefs use: beliefsToAdd.add(Literal)
     * To delete beliefs use: beliefsToDelete(literal)
     * 
     * Once you are happy with what your function will do use:
     * boolean success = runAction(Agent ag, String action, List<Literal> predicate, List<Literal> beliefsToAdd, List<Literal> beliefsToDelete)
     * if you are not using a variable replace with null.
     * 
     * Followed by
     * if(success) {return true;} else {return false;}
     */

    // Action 1
    private boolean doChores(Agent ag, String action) {
        beliefsToAdd.add(Literal.parseLiteral("hasMoney"));
        beliefsToAdd.add(Literal.parseLiteral("parentsHappy"));

        boolean success = runAction(ag, action, null, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }

    // Action 2
    private boolean buyPhone(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("hasMoney"));
        beliefsToAdd.add(Literal.parseLiteral("hasPhone"));
        beliefsToDelete.add(Literal.parseLiteral("hasMoney"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, beliefsToDelete);
        if(success) {return true;} else {return false;}
    }

    // Action 3
    private boolean earnSalary(Agent ag, String action) {
        beliefsToAdd.add(Literal.parseLiteral("hasMoney"));

        boolean success = runAction(ag, action, null, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }

    // Action 4
    private boolean textFriend(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("onPhone"));
        predicate.add(Literal.parseLiteral("hasPhone"));
        beliefsToAdd.add(Literal.parseLiteral("messageSent"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }

    // Action 5
    private boolean usePhone(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("hasPhone"));
        beliefsToAdd.add(Literal.parseLiteral("onPhone"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }

    // Action 6
    private boolean goOffPhone(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("hasPhone"));
        predicate.add(Literal.parseLiteral("onPhone"));

        beliefsToDelete.add(Literal.parseLiteral("onPhone"));
        Boolean success = runAction(ag, action, predicate, null, beliefsToDelete);
        if(success) {return true;} else {return false;}
    }
    // Action 7
    private boolean goToGym(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("motivated"));
        predicate.add(Literal.parseLiteral("inCar"));

        beliefsToAdd.add(Literal.parseLiteral("atGym"));
        beliefsToAdd.add(Literal.parseLiteral("hungry"));
        beliefsToAdd.add(Literal.parseLiteral("happy"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }
    // Action 8
    private boolean getInCar(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("hasCar"));

        beliefsToAdd.add(Literal.parseLiteral("inCar"));
        beliefsToDelete.add(Literal.parseLiteral("atHome"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, beliefsToDelete);
        if(success) {return true;} else {return false;}
    }
    // Action 9
    private boolean goToWork(Agent ag, String action) {
        predicate.add(Literal.parseLiteral("inCar"));

        beliefsToAdd.add(Literal.parseLiteral("atWork"));
        beliefsToAdd.add(Literal.parseLiteral("tired"));
        beliefsToAdd.add(Literal.parseLiteral("bossHappy"));

        Boolean success = runAction(ag, action, predicate, beliefsToAdd, null);
        if(success) {return true;} else {return false;}
    }
}
