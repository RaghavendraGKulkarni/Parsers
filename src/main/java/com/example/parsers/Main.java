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
        System.out.println(String.format("There are %d terminals and %d non-terminals", g.terminals.size() - 2, g.nonTerminals.size()));        
        System.out.println(String.format("There are %d rules", g.rules.size()));
        g.leftRecursion();
        g.printGrammar();
        g.saveGrammar(outputPath);
    }
}