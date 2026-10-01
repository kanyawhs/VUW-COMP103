// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 5
 * Name: Kanya Farley
 * Username: farleykany
 * ID: 300693857
 * Ver: 1/10
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
     *  
     *  Every case for every expression checks if child node count is within boundaries for the particular expression.
     */
    public double evaluate(GTNode<ExpElem> expr){
        if (expr==null){
            return Double.NaN;
        }

        /*# YOUR CODE HERE */
        ExpElem elem = expr.getItem();
        /* may be inefficient to make new variables for same things?? */
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
                if (expr.numberOfChildren() > 0) {
                    double total = 0;
                    for (int i = 0; i < expr.numberOfChildren(); i++) {
                        total += evaluate(expr.getChild(i)); // add up every following expression
                    }
                    return total;
                } else {
                    UI.println("Input invalid.");
                    UI.println("'+' must have at least 1 expression following it in parentheses");
                    return Double.NaN;
                }

            case "-" :
                if (expr.numberOfChildren() > 0) {
                    double first = (evaluate(expr.getChild(0))); // first operand
                    for (int i = 1; i < expr.numberOfChildren(); i++) {
                        first -= evaluate(expr.getChild(i)); // subtract all following expressions from first child
                    }
                    return first;
                } else {
                    UI.println("Input invalid.");
                    UI.println("'-' must have at least 1 expression following it in parentheses");
                    return Double.NaN;
                }
            case "*" :
                if (expr.numberOfChildren() > 0) {
                    double eval = (evaluate(expr.getChild(0))); // to start multiplication
                    for (int i = 1; i < expr.numberOfChildren(); i++) {
                        eval *= evaluate(expr.getChild(i)); // multiply every following expression, starting with base number
                    }
                    return eval;
                } else {
                    UI.println("Input invalid.");
                    UI.println("'*' must have at least 1 expression following it in parentheses");
                    return Double.NaN;
                }
            case "/" :
                if (expr.numberOfChildren() > 0) {
                    double evaluation = (evaluate(expr.getChild(0))); // to divide
                    for (int i = 1; i < expr.numberOfChildren(); i++) {
                        evaluation /= evaluate(expr.getChild(i)); // repeatedly divide first expression with following expressions
                    }
                    return evaluation;
                } else {
                    UI.println("Input invalid.");
                    UI.println("'/' must have at least 1 expression following it in parentheses");
                    return Double.NaN;
                }

            // handle more complex operators with children 
            case "^" :
                if (expr.numberOfChildren() == 2) {
                    double result = Math.pow(evaluate(expr.getChild(0)), evaluate(expr.getChild(1))); // find power of first and second following expressions
                    return result;
                } else {
                    UI.println("Input invalid.");
                    UI.println("'^' can only have two expressions following it in parentheses.");
                    return Double.NaN;
                }
            case "sqrt" :
                return Math.sqrt(evaluate(expr.getChild(0)));
            case "log" : 
                if (expr.numberOfChildren() == 1) {
                    double log = Math.log10(evaluate(expr.getChild(0))); // gets log10 of single child node
                    return log;
                } else if (expr.numberOfChildren() == 2) {
                    double value = Math.log(evaluate(expr.getChild(0))); // evaluates log of first operand
                    double base = Math.log(evaluate(expr.getChild(1))); // evaluates log of second operand
                    return value / base; // second operand treated as base
                } else {
                    UI.println("Input invalid.");
                    UI.println("'log' can only have either one or two expressions following it in parentheses.");
                    return Double.NaN;
                }
            case "ln" :
                if (expr.numberOfChildren() == 1) {
                    return Math.log(evaluate(expr.getChild(0)));
                } else {
                    UI.println("Input invalid.");
                    UI.println("'ln' can only take 1 expression following it in parentheses");
                    return Double.NaN;
                }
            // trigonometric functions
            case "sin" :
                if (expr.numberOfChildren() == 1) {
                    return Math.sin(evaluate(expr.getChild(0)));
                } else {
                    UI.println("Input invalid.");
                    UI.println("'sin' can only take 1 expression following it in parentheses");
                    return Double.NaN;
                }
            case "cos" :
                if (expr.numberOfChildren() == 1) {
                    return Math.cos(evaluate(expr.getChild(0)));
                } else {
                    UI.println("Input invalid.");
                    UI.println("'cos' can only take 1 expression following it in parentheses");
                    return Double.NaN;
                }
            case "tan" :
                if (expr.numberOfChildren() == 1) {
                    return Math.tan(evaluate(expr.getChild(0)));
                } else {
                    UI.println("Input invalid.");
                    UI.println("'tan' can only take 1 expression following it in parentheses");
                    return Double.NaN;
                }
            case "dist" :
                if (expr.numberOfChildren() == 4) { // for 2d space
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double x2 = evaluate(expr.getChild(2));
                    double y2 = evaluate(expr.getChild(3));
                    
                    double dist = Math.sqrt(((x2 - x1)*(x2 - x1)) + ((y2 - y1)*(y2 - y1))); // formula for euclidean distance
                    return dist;
                } else if (expr.numberOfChildren() == 6) { // for 3d space
                    // something wrong here fr
                    double x1 = evaluate(expr.getChild(0));
                    double y1 = evaluate(expr.getChild(1));
                    double z1 = evaluate(expr.getChild(2));
                    double x2 = evaluate(expr.getChild(3));
                    double y2 = evaluate(expr.getChild(4));
                    double z2 = evaluate(expr.getChild(5));
                    
                    double dist = Math.sqrt(((x2 - x1)*(x2 - x1)) + ((y2 - y1)*(y2 - y1)) + ((z2 - z1)*(z2 - z1))); // formula for euclidean distance
                } else {
                    UI.println("Input invalid.");
                    UI.println("'dist' can only take exactly 4 expressions (2d space) or exactly 6 expressions (3d space) following it in parentheses");
                    return Double.NaN;
                }
            case "avg" :
                if (expr.numberOfChildren() > 0) {
                    double sum = 0;
                    for (int i = 0; i < expr.numberOfChildren(); i++) {
                        sum += evaluate(expr.getChild(i)); // add up every following expression
                    }
                    return sum / expr.numberOfChildren();
                } else {
                    UI.println("Input invalid.");
                    UI.println("'avg' must have at least 1 expression following it in parentheses");
                    return Double.NaN;
                }
            default :
                UI.println("Invalid expression found. Please try again.");
                return Double.NaN;
        }
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

