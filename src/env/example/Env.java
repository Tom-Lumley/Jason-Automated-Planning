package example;

import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.environment.*;

/**
 * The Env class represents the environment in which the agents of the system operate.
 * It extends the jason.environment.Environment class.
 */
public class Env extends Environment {
    /**
     * Executes the specified action in the environment for the given agent.
     * 
     * @param agName The name of the agent performing the action.
     * @param action The action to be executed by the agent.
     * @return True if the action was executed successfully, false otherwise.
     */
    @Override
    public boolean executeAction(String agName, Structure action) { 
        Agent ag = acquireAgent(agName);     
        Action act = new Action();

        boolean success = act.startAction(ag, action.toString());

        if (true) { informAgsEnvironmentChanged();}

        if(success) {return true;}else {return false;} 
    }

    /**
     * Acquires the Agent object corresponding to the given agent name.
     * 
     * @param agName The name of the agent to acquire.
     * @return The Agent object corresponding to the specified agent name.
     */
    private Agent acquireAgent(String agName) {
        Agent agent = null;
        try {agent = getEnvironmentInfraTier().getRuntimeServices().getAgentSnapshot(agName);} catch (Exception e) {e.printStackTrace();} 
        return agent;
    }

    /**
     * Called before the MAS execution with the arguments informed in .mas2j.
     * 
     * @param args The arguments passed to the MAS execution.
     */
    @Override
    public void init(String[] args) {super.init(args);}

    /**
     * Called before the end of MAS execution.
     */
    @Override
    public void stop() {super.stop();}
}
