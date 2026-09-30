// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 5
 * Name: Kanya Farley
 * Username: farleykany
 * ID: 300693857
 * Ver: 30/9
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Calculator for Cambridge-Polish Notation expressions
 * (see the description in the assignment page)
 * User can type in an expression (in CPN) and the program
 * will compute and print out the value of the expression.
 * The template provides the method to read an expression and turn it into a tree.
 * You have to write the method to evaluate an expression tree.
 *  and also check and report certain kinds of invalid expressions
 */

public class CPNCalculator{

    /**
     * Setup GUI then run the calculator
     */
    public static void main(String[] args){
        CPNCalculator calc = new CPNCalculator();
        calc.setupGUI();
        calc.runCalculator();
    }

    /** Setup the GUI */
    public void setupGUI(){
        UI.addButton("Clear", UI::clearText); 
        UI.addButton("Quit", UI::quit); 
        UI.setDivider(1.0);
    }

    /**
     * Run the calculator:
     * loop forever:  (a REPL - Read Eval Print Loop)
     *  - read an expression,
     *  - evaluate the expression,
     *  - print out the value
     * Invalid expressions could cause errors when reading or evaluating
     * The try-catch prevents these errors from crashing the program - 
     *  the error is caught, and a message printed, then the loop continues.
     */
    public void runCalculator(){
        UI.println("Enter expressions in pre-order format with spaces");
        UI.println("eg   ( * ( + 4 5 8 3 -10 ) 7 ( / 6 4 ) 18 )");
        while (true){
            UI.println();
            try {
                GTNode<ExpElem> expr = readExpr();
                double value = evaluate(expr);
                UI.println(" -> " + value);
            }catch(Exception e){UI.println("Something went wrong! "+e);}
        }
    }

    /**
     * Evaluate an expression and return the value
     * Returns Double.NaN if the expression is invalid in some way.
     * If the node is a number
     *  => just return the value of the number
     * or it is a named constant
     *  => return the appropriate value
     * or it is an operator node with children
     *  => evaluate all the children and then apply the operator.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        /*# YOUR CODE HERE */
        ExpElem elem = expr.getItem();
        String[] constants = {"PI", "E"};
        String[] applicableOperators = {"+", "-", "*", "/", /*"^", "sqrt", "log", "ln", "sin", "cos", "tan", "dist", "avg"*/};
        switch (elem.operator) {
                // handle numbers
            case "#" :
                return elem.value;

                // handle constants
            case "PI" :
                return Math.PI;
            case "E" :
                return Math.E;

                // handle applicable operators with children
            case "+" :
                    ArrayList<Double> toAdd = new ArrayList<>();
                    for (int i = 0; i <= expr.numberOfChildren(); i++) {
                        toAdd.add(evaluate(expr.getChild(i))); // recursively gets evaluation of each child
                    }
                    double total = 0;
                    for (double v : toAdd) {
                        total += v;
                    }
                return total;

            case "-" :
                    double first = expr.getChild(0).getItem().value; // first operand
                    ArrayList<Double> toSubtract = new ArrayList<>();
                    for (int i = 1; i <= expr.numberOfChildren(); i++) {
                        toSubtract.add(evaluate(expr.getChild(i)));
                    }
                    for (double v : toSubtract) {
                        first -= v;
                    }
                return first;
            case "*" : // ok chat this one a little harder
                    ArrayList<Double> toMultiply = new ArrayList<>();
                    for (int i = 0; i <= expr.numberOfChildren(); i++) {
                        toMultiply.add(evaluate(expr.getChild(i)));
                    }
                    double eval = 0;
                    for (double v : toMultiply) {
                        
                    }

            case "/" :

        }
        return Double.NaN;
    }

    /**
     * Reads an expression from the user and constructs the tree.
     */ 
    public GTNode<ExpElem> readExpr(){
        String expr = UI.askString("expr:");
        return readExpr(new Scanner(expr));   // the recursive reading method
    }

    /**
     * Recursive helper method.
     * Uses the hasNext(String pattern) method for the Scanner to peek at next token
     */
    public GTNode<ExpElem> readExpr(Scanner sc){
        if (sc.hasNextDouble()) {                     // next token is a number: return a new node
            return new GTNode<ExpElem>(new ExpElem(sc.nextDouble()));
        }
        else if (sc.hasNext("\\(")) {                 // next token is an opening bracket
            sc.next();                                // read and throw away the opening '('
            ExpElem opElem = new ExpElem(sc.next());  // read the operator
            GTNode<ExpElem> node = new GTNode<ExpElem>(opElem);  // make the node, with the operator in it.
            while (! sc.hasNext("\\)")){              // loop until the closing ')'
                GTNode<ExpElem> child = readExpr(sc); // read each operand/argument
                node.addChild(child);                 // and add as a child of the node
            }
            sc.next();                                // read and throw away the closing ')'
            return node;
        }
        else {                                        // next token must be a named constant (PI or E)
            // make a token with the name as the "operator"
            return new GTNode<ExpElem>(new ExpElem(sc.next()));
        }
    }

}

