package example;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The PDDLGenerator class is responsible for generating PDDL problem files based on given beliefs and predicates.
 */
public class PDDLGenerator {

    /**
     * Generates a PDDL problem file based on the specified agent name, beliefs, and predicates.
     * 
     * @param agName The name of the agent.
     * @param beliefs The list of beliefs to be included in the PDDL problem.
     * @param predicates The list of predicates representing the goal state of the PDDL problem.
     */
    public static void generate(String agName, List<String> beliefs, List<String> predicates) {
        try {
            // Specify the file path for the PDDL file
            String filePath = agName+"problem.pddl";
            //System.out.println(filePath);

            // Create a FileWriter
            FileWriter writer = new FileWriter(filePath);

            // Define your custom data for each section
            String domainName = "shoppingdomain"; // Can be anything really
            String[] initialState = beliefs.toArray(new String[0]);

            // Write PDDL content to the file using custom data
            writePDDLHeader(writer, "textingproblem", domainName);
            //writePDDLObjects(writer, objectDeclarations);
            writePDDLInitialState(writer, initialState, preprocessPredicates(predicates));
            writePDDLGoalState(writer, preprocessPredicates(predicates));
            writePDDLFooter(writer);

            // Close the FileWriter
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the PDDL header to the file.
     * 
     * @param writer The FileWriter object.
     * @param problemName The name of the PDDL problem.
     * @param domainName The name of the PDDL domain.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePDDLHeader(FileWriter writer, String problemName, String domainName) throws IOException {
        writer.write("(define (problem " + problemName + ")\n");
        writer.write("  (:domain " + domainName + ")\n");
    }


    /**
     * Writes the PDDL initial state to the file.
     * 
     * @param writer The FileWriter object.
     * @param initialState The array of initial state predicates.
     * @param goalState The list of goal state predicates.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePDDLInitialState(FileWriter writer, String[] initialState, List<String> goalState) throws IOException {
        writer.write("  (:init\n");
        if(initialState.length >= 1) {
            for (String initialStateFact : initialState) {
                writer.write("    (" + initialStateFact + ")\n");
            }
        } else {
            for (String goalStateFact :goalState) {
                writer.write("    (not (" + goalStateFact + "))\n");
            }       
        }
        writer.write("    (" + "dummyPredicate" + ")\n");   
        writer.write("  )\n");
    }

    /**
     * Writes the PDDL goal state to the file.
     * 
     * @param writer The FileWriter object.
     * @param predicates The list of goal state predicates.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePDDLGoalState(FileWriter writer, List<String> predicates) throws IOException {
        writer.write("  (:goal\n");
        writer.write("    (and\n");

        for (String predicate : predicates) {
            writer.write("      (" + predicate + ")\n");
        }

        writer.write("    )\n");
        writer.write("  )\n");
    }

    /**
     * Writes the PDDL footer to the file.
     * 
     * @param writer The FileWriter object.
     * @throws IOException If an I/O error occurs.
     */
    private static void writePDDLFooter(FileWriter writer) throws IOException {
        writer.write(")\n");
    }

    /**
     * Preprocesses the list of predicates, removing parentheses and splitting on '&'.
     * 
     * @param predicates The list of predicates to preprocess.
     * @return The preprocessed list of predicates.
     */
    private static List<String> preprocessPredicates(List<String> predicates) {
        List<String> formattedPredicates = new ArrayList<>();
    
        for (String predicate : predicates) {
            // Remove parentheses only if they are around the entire predicate
            String withoutParentheses = predicate.replaceAll("^\\((.*)\\)$", "$1").trim();
    
            // Split the predicate if it contains "&" and trim each part
            if (withoutParentheses.contains("&")) {
                String[] parts = withoutParentheses.split("&");
                for (String part : parts) {
                    formattedPredicates.add(part.trim());
                }
            } else {
                // If there is no "&", add the predicate directly
                formattedPredicates.add(withoutParentheses);
            }
        }
        return formattedPredicates;
    }
}
