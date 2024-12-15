// Declare the project package
package com.example.parsers;

// Import the required java packages
import java.io.InputStream;

// Define the main class
public class Main {

    // Define the main function
    public static void main(String[] args) {

        // Declare an InputStream instance using the file name
        String inputPath = "Input.txt";
        InputStream inputFile = Main.class.getClassLoader().getResourceAsStream(inputPath);
        
        // Declare an InputStream instance using the file name
        String outputPath = "Output.txt";

        // Load the grammar
        grammar g = grammar.loadGrammar(inputFile);
        
        // Display the grammar and grammar attributes
        g.printGrammar();
        System.out.println(String.format("There are %d terminals, %d non-terminals and %d rules.", g.terminals.size() - 2, g.nonTerminals.size(), g.rules.size()));
        
        // Eliminate Left Factoring
        g.leftFactor();
        g.printGrammar();

        // Eliminate Left Recursion
        g.leftRecursion();
        g.printGrammar();
        g.saveGrammar(outputPath);
    }
}