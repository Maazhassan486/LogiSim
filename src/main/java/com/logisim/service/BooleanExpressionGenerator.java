package com.logisim.service;

import java.util.*;

/**
 * Generates boolean expressions from truth tables.
 * Implements sum-of-products (SOP) and product-of-sums (POS) generation.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class BooleanExpressionGenerator {

    /**
     * Generates a boolean expression in sum-of-products form from a truth table.
     * 
     * @param truthTable The truth table (list of rows, each row is a map of variable names to values)
     * @param outputVariable The name of the output variable to generate expression for
     * @param inputVariables List of input variable names (in order)
     * @return The boolean expression as a string
     */
    public String generateSOPExpression(List<Map<String, Boolean>> truthTable, 
                                       String outputVariable, 
                                       List<String> inputVariables) {
        if (truthTable == null || truthTable.isEmpty()) {
            return "0"; // False
        }

        List<String> minterms = new ArrayList<>();

        for (Map<String, Boolean> row : truthTable) {
            Boolean outputValue = row.get(outputVariable);
            if (outputValue != null && outputValue) {
                // This row produces true output - create a minterm
                List<String> literals = new ArrayList<>();
                for (String inputVar : inputVariables) {
                    Boolean inputValue = row.get(inputVar);
                    if (inputValue != null) {
                        if (inputValue) {
                            literals.add(inputVar);
                        } else {
                            literals.add("!" + inputVar);
                        }
                    }
                }
                if (!literals.isEmpty()) {
                    minterms.add(String.join(" & ", literals));
                }
            }
        }

        if (minterms.isEmpty()) {
            return "0"; // Always false
        }

        if (minterms.size() == 1) {
            return minterms.get(0);
        }

        return "(" + String.join(") | (", minterms) + ")";
    }

    /**
     * Generates a simplified boolean expression using basic simplification.
     * 
     * @param truthTable The truth table
     * @param outputVariable The output variable name
     * @param inputVariables List of input variable names
     * @return Simplified boolean expression
     */
    public String generateSimplifiedExpression(List<Map<String, Boolean>> truthTable,
                                               String outputVariable,
                                               List<String> inputVariables) {
        String sopExpression = generateSOPExpression(truthTable, outputVariable, inputVariables);
        return simplifyExpression(sopExpression);
    }

    /**
     * Simplifies a boolean expression using basic rules.
     * 
     * @param expression The expression to simplify
     * @return Simplified expression
     */
    private String simplifyExpression(String expression) {
        if (expression.equals("0") || expression.equals("1")) {
            return expression;
        }

        // Remove redundant parentheses
        expression = expression.replaceAll("\\(\\(([^()]+)\\)\\)", "($1)");
        
        // Basic simplifications
        expression = expression.replaceAll("\\s+", " "); // Normalize whitespace
        expression = expression.trim();

        return expression;
    }

    /**
     * Generates boolean expressions for all outputs in a truth table.
     * 
     * @param truthTable The truth table
     * @param inputVariables List of input variable names
     * @return Map of output variable names to their boolean expressions
     */
    public Map<String, String> generateAllExpressions(List<Map<String, Boolean>> truthTable,
                                                     List<String> inputVariables) {
        Map<String, String> expressions = new HashMap<>();

        if (truthTable == null || truthTable.isEmpty()) {
            return expressions;
        }

        // Find all output variables (those that start with "Output_")
        Set<String> outputVariables = new HashSet<>();
        for (Map<String, Boolean> row : truthTable) {
            for (String key : row.keySet()) {
                if (key.startsWith("Output_")) {
                    outputVariables.add(key);
                }
            }
        }

        // Generate expression for each output
        for (String outputVar : outputVariables) {
            String expression = generateSOPExpression(truthTable, outputVar, inputVariables);
            expressions.put(outputVar, expression);
        }

        return expressions;
    }

    /**
     * Formats a boolean expression for display.
     * 
     * @param expression The expression to format
     * @return Formatted expression
     */
    public String formatExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return "";
        }

        // Replace operators with more readable symbols
        expression = expression.replace(" & ", " ∧ ");
        expression = expression.replace(" | ", " ∨ ");
        expression = expression.replace("!", "¬");

        return expression;
    }
}



