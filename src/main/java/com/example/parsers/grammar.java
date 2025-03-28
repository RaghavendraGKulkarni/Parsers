// Declare the project package
package com.example.parsers;

// Import the required java packages
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

// Define the required data type
enum type {
    terminal, nonTerminal
}

// Define a literal class
class literal {
    
    // Declare the literal attributes
    public String symbol;
    public type t;
    
    // Define a constructor for the literal class
    public literal(String symbol, type t) {
        this.symbol = symbol;
        this.t = t;
    }
}

// Define a rule class
class rule {
    
    // Declare the rule attributes
    public literal lhs;
    public List<literal> rhs;
    
    // Define a constructor for the rule class
    public rule(literal lhs, List<literal> rhs) {
        this.lhs = lhs;
        this.rhs = new ArrayList<literal>(rhs);
    }
}

// Define the grammar class
public class grammar {
    
    // Declare the grammar attributes
    public List<literal> terminals;
    public List<literal> nonTerminals;
    public List<rule> rules;
    public literal start;
    public static literal epsilon;
    public static literal dollar;
    
    // Define a constructor for the grammar class
    public grammar(String[] terminals, String[] nonTerminals) {
        
        // Set the terminals of the grammar
        this.terminals = new ArrayList<literal>();
        for(int i = 0; i < terminals.length; i++)
            this.terminals.add(new literal(terminals[i], type.terminal));
        
        // Set the non-terminals of the grammar
            this.nonTerminals = new ArrayList<literal>();
        for(int i = 0; i < nonTerminals.length; i++)
            this.nonTerminals.add(new literal(nonTerminals[i], type.nonTerminal));
        
        // Initiate the array list of grammar rules
        this.rules = new ArrayList<rule>();

        // Define the epsilon and dollar symbols as special terminals and add them to the grammar
        grammar.epsilon = new literal("\u03B5", type.terminal);
        grammar.dollar = new literal("$", type.terminal);
        this.terminals.add(grammar.epsilon);
        this.terminals.add(grammar.dollar);
    }
    
    // Define a method to search a given literal symbol
    public literal search(String symbol, type t) {
        
        // Search for the literal in terminals
        if(t == type.terminal) {
            for (literal terminal : this.terminals) {
                if(terminal.symbol.equals(symbol))
                    return terminal;
            }
        }

        // Search for the literal in non-terminals
        else if(t == type.nonTerminal) {
            for (literal nonTerminal : this.nonTerminals) {
                if(nonTerminal.symbol.equals(symbol))
                    return nonTerminal;
            }
        }

        // Return NULL on failure
        return null;
    }
    
    // Define a method to parse a rule
    public void addRule(String s) {

        // Split the rule into LHS and RHS using "->"
        String[] tokens = s.split("->");
        String production = tokens[0], token;

        // Split the RHS into one more RHS using "|"
        String[] expansions = tokens[1].split("\\|");
        
        // Verify that the LHS is a non-terminal
        literal lhs = this.search(production, type.nonTerminal), rhs;
        if(lhs == null) {
            System.out.println(String.format("%s is not a non-terminal.", production));
            return;
        }

        // Parse the RHS list into literals
        for(String expansion : expansions) {
            
            token = "";
            List<literal> rhsList = new ArrayList<literal>();
            
            // Iteratively append a character to the token and check for a valid literal
            for(int i = 0; i < expansion.length(); i++) {
                token += expansion.charAt(i);
                rhs = this.search(token, type.terminal);
                if(rhs == null)
                    rhs = this.search(token, type.nonTerminal);
                if(rhs == null)
                    continue;
                rhsList.add(rhs);
                token = "";
            }

            // Handle the invalid token case
            if(token.length() > 0)
                System.out.println(String.format("Unidentified literal in %s->%s not a non-terminal.", production, expansion));
            
            // Add the rule to the list of rules
            else
                this.rules.add(new rule(lhs, rhsList));
        }
    }
    
    // Define a method to remove left factoring from the grammar
    public void leftFactoring() {

        System.out.println("Eliminating left factors...!!!");
        
        int i = 0;
        while(i < this.rules.size()) {

            // Check for left factors
            List<Integer> factors = new ArrayList<Integer>();
            List<Integer> nonFactors = new ArrayList<Integer>();
            for(int j = i + 1; j < this.rules.size(); j++) {
                if(this.rules.get(i).lhs.symbol.equals(this.rules.get(j).lhs.symbol)) {
                    if(this.rules.get(i).rhs.get(0).symbol.equals(this.rules.get(j).rhs.get(0).symbol))
                        factors.add(j);
                    else
                        nonFactors.add(j);
                }
            }

            // Consider the rule as A -> Bα1 | Bα2 | ... | Bαm | ß1 | ß2 | .... | ßn
            // Gather the αs for the left factoring
            if(factors.size() > 0) {

                // Compute the longest common prefix on the RHS side for all the factors
                int minLength = this.rules.get(i).rhs.size();
                for(int j = 0; j < factors.size(); j++)
                    minLength = (minLength > this.rules.get(factors.get(j)).rhs.size()) ? (this.rules.get(factors.get(j)).rhs.size()) : (minLength);     
                List<literal> prefix = new ArrayList<literal>();
                boolean same;
                for(int j = 0; j < minLength; j++) {
                    same = true;
                    for(int k = 0; k < factors.size(); k++)
                        if(!this.rules.get(i).rhs.get(j).equals(this.rules.get(factors.get(k)).rhs.get(j)))
                            same = false;
                    if(same)
                        prefix.add(this.rules.get(i).rhs.get(j));
                    else
                        break;
                }

                // Rewrite the current rule as A -> BA1 | ß1 | ß2 | .... | ßn
                literal lhs = this.rules.get(i).lhs;
                literal dupl = new literal(this.rules.get(i).lhs.symbol + "1", type.nonTerminal);
                prefix.add(dupl);
                this.nonTerminals.add(dupl);

                // Add the new rule A1 -> α1 | α2 | ... | αm
                rule r = new rule(lhs, prefix);
                factors.add(0, i);
                for(Integer index : factors) {
                    this.rules.get(index).lhs = dupl;
                    for(int j = 0; j < prefix.size() - 1; j++)
                        this.rules.get(index).rhs.remove(0);
                    if(this.rules.get(index).rhs.size() == 0)
                        this.rules.get(index).rhs.add(epsilon);
                }
                this.rules.add(i, r);

                // Reorder the rules
                for(Integer index : nonFactors) {
                    int j = index + 1;
                    while(this.rules.get(j).lhs.symbol.equals(this.rules.get(j - 1).lhs.symbol) == false) {
                        rule temp = this.rules.get(j);
                        this.rules.set(j, this.rules.get(j - 1));
                        this.rules.set(j - 1, temp);
                        j--;
                    }
                }
            }
            i++;
        }

        return;
    }

    // Define a method to remove left recursion from the grammar
    public void leftRecursion() {
        
        System.out.println("Eliminating left recursion...!!!");

        int i = 0;
        while(i < this.rules.size()) {

            // Check for left recursion
            if(this.rules.get(i).lhs.symbol == this.rules.get(i).rhs.get(0).symbol) {
                
                // Consider the rule as A -> Aα | ß1 | ß2 | .... | ßn
                // Gather the α for the left recursion
                List<literal> α = new ArrayList<literal>();
                for(int j = 1; j < this.rules.get(i).rhs.size(); j++)
                    α.add(this.rules.get(i).rhs.get(j));
                
                // Gather all the ß rule indices for the left recursion
                List<Integer> ßIDs = new ArrayList<Integer>();
                for(int j = 0; j < this.rules.size(); j++)
                    if(this.rules.get(j).lhs.symbol == this.rules.get(i).lhs.symbol && this.rules.get(j).lhs.symbol != this.rules.get(j).rhs.get(0).symbol)
                        ßIDs.add(j);
                
                // Handle the case of missing ß production
                if(ßIDs.size() == 0) {
                    System.out.println("Cannot remove left recursion of " + this.rules.get(i).lhs.symbol + " due to missing ß production");
                    i++;
                    continue;
                }

                // Rewrite the rule as A -> (ß1 | ß2 | .... | ßn)A2 and A2 -> αA2 | ε
                literal dupl = new literal(this.rules.get(i).lhs.symbol + "2", type.nonTerminal);
                this.nonTerminals.add(dupl);
                for(Integer index : ßIDs)
                    this.rules.get(index).rhs.add(dupl);
                this.rules.get(i).lhs = dupl;
                this.rules.get(i).rhs.clear();
                this.rules.get(i).rhs.addAll(α);
                this.rules.get(i).rhs.add(dupl);
                for(int j = i; j < ßIDs.get(ßIDs.size() - 1); j++) {
                    rule temp = this.rules.get(j);
                    this.rules.set(j, this.rules.get(j + 1));
                    this.rules.set(j + 1, temp);
                }
                List<literal> rhsList = new ArrayList<literal>();
                rhsList.add(grammar.epsilon);
                this.rules.add(ßIDs.get(ßIDs.size() - 1) + 1, new rule(dupl, rhsList));
            }
            i++;
        }

        return;
    }

    // A method to print the grammar on the console
    public void printGrammar() {
        System.out.println("The Grammar is");
        for(int i = 0; i < this.rules.size(); i++) {
            System.out.print(this.rules.get(i).lhs.symbol + "->");
            for(int j = 0; j < this.rules.get(i).rhs.size(); j++)
                System.out.print(this.rules.get(i).rhs.get(j).symbol);
            System.out.println("");
        }
        return;
    }

    // A method to save the grammar to an output file
    public void saveGrammar(String outputPath) {
        
        // Try to open the file and handle the error
        try(OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream(outputPath), "UTF-8")) {

            // Write the grammar to the file
            for(int i = 0; i < this.rules.size(); i++) {
                output.write((this.rules.get(i).lhs.symbol + "->"));
                for(int j = 0; j < this.rules.get(i).rhs.size(); j++)
                    output.write((this.rules.get(i).rhs.get(j).symbol));
                    output.write("\n");
            }

        } catch(IOException e) {
            System.out.println("Output File Open Error...!!!");
        }
        return;
    }

    // Define a static method to load the grammar from the input text file
    public static grammar loadGrammar(String inputPath) {
        
        // File handler for the input file path
        InputStream inputFile = Main.class.getClassLoader().getResourceAsStream(inputPath);

        // Try to open the file and handle the error
        try(BufferedReader input = new BufferedReader(new InputStreamReader(inputFile))) {
            
            // Read the first line as delimiter
            String delim = input.readLine();
            if(delim == null) {
                System.out.println("Empty File Error");
                return null;
            }

            // Read the second line as terminals
            String line = input.readLine();
            String[] terminals = line.split(delim);
            
            // Read the third line as non-terminals
            line = input.readLine();
            String[] nonTerminals = line.split(delim);

            // Initiate the grammar using terminals and non-terminals
            grammar g = new grammar(terminals, nonTerminals);

            // Read the remaining lines for rules and start symbol
            do {

                // Read the next line and handle EOF
                line = input.readLine();
                if(line == null)
                    continue;
                
                // Check if the line is a production rule and add the rule
                if(line.contains("->")) {
                    g.addRule(line);
                    continue;
                }

                // Consider the line as a start symbol and validate it as a non-terminal
                literal start = g.search(line, type.nonTerminal);
                if(start == null)
                    System.out.println(String.format("Start symbol %s not identified as a non-terminal", line));
                else
                    g.start = start;
            
                }while(line != null);
            
            // Return the grammar
            return g;
        }

        // Handle the file opening exception
        catch(IOException e) {
            System.out.println("Input File Open Error...!!!");
            return null;
        }
    }
}