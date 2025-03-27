// Declare the project package
package com.example.parsers;

// Define the main class
public class Main {

    // Define the main function
    public static void main(String[] args) {

        // Declare an InputStream instance using the file name
        String inputPath = "Input.txt";
        
        // Declare an InputStream instance using the file name
        String outputPath = "Output.txt";

        // Load the grammar
        grammar g = grammar.loadGrammar(inputPath);
        
        // Display the grammar and grammar attributes
        g.printGrammar();
        System.out.println(String.format("There are %d terminals, %d non-terminals and %d rules.", g.terminals.size() - 2, g.nonTerminals.size(), g.rules.size()));
        
        // Eliminate Left Factoring
        g.leftFactoring();
        g.printGrammar();

        // Eliminate Left Recursion
        g.leftRecursion();
        g.printGrammar();

        // Save the grammar
        g.saveGrammar(outputPath);
    }
}