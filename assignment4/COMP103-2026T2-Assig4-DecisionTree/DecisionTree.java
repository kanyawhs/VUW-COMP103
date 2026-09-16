// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 4
 * Name: Kanya Farley
 * Username: farleykany
 * ID: 30069
 * Version: 16/9
 */

/**
 * Implements a decision tree that asks a user yes/no questions to determine a decision.
 * Eg, asks about properties of an animal to determine the type of animal.
 * 
 * A decision tree is a tree in which all the internal nodes have a question, 
 * The answer to the question determines which way the program will
 *  proceed down the tree.  
 * All the leaf nodes have the decision (the kind of animal in the example tree).
 *
 * The decision tree may be a predermined decision tree, or it can be a "growing"
 * decision tree, where the user can add questions and decisions to the tree whenever
 * the tree gives a wrong answer.
 *
 * In the growing version, when the program guesses wrong, it asks the player
 * for another question that would help it in the future, and adds it (with the
 * correct answers) to the decision tree. 
 *
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.awt.Color;

public class DecisionTree {

    public DTNode theTree;    // root of the decision tree;

    /**
     * Setup the GUI and make a sample tree
     */
    public static void main(String[] args){
        DecisionTree dt = new DecisionTree();
        dt.setupGUI();
        dt.loadTree("sample-animal-tree.txt");
    }

    /**
     * Set up the interface
     */
    public void setupGUI(){
        UI.addButton("Load Tree", ()->{loadTree(UIFileChooser.open("File with a Decision Tree"));});
        UI.addButton("Print Tree", this::printTree);
        UI.addButton("Run Tree", this::runTree);
        UI.addButton("Grow Tree", this::growTree);
        // UI.addButton("Save Tree", this::saveTree);  // for completion
        // UI.addButton("Draw Tree", this::drawTree);  // for challenge
        UI.addButton("Reset", ()->{loadTree("sample-animal-tree.txt");});
        UI.addButton("Quit", UI::quit);
        UI.setDivider(0.5);
    }

    /**  
     * Print out the contents of the decision tree in the text pane.
     * The root node should be at the top, followed by its "yes" subtree,
     * and then its "no" subtree.
     * Needs a recursive "helper method" which is passed a node. 
     * 
     * COMPLETION:
     * Each node should be indented by how deep it is in the tree.
     * The recursive "helper method" is passed a node and an indentation string.
     *  (The indentation string will be a string of space characters)
     */
    public void printTree(){
        UI.clearText();
        /*# YOUR CODE HERE */
        Stack<DTNode> toDo = new Stack<DTNode>();
        toDo.push(theTree);
        // second subtree (n child to root) goes in post-order?
        while (!toDo.isEmpty()) {
            DTNode n = toDo.pop();
            printSubTree(n);
            if (n.getNo() != null && !n.getNo().isAnswer()) { toDo.push(n.getNo());}
            if (n.getYes() != null && !n.getYes().isAnswer()) { toDo.push(n.getYes());}
        }
    }

    /**
     * Recursive helper method for above
     */
    private void printSubTree(DTNode n) {
        if (n != null) {
            UI.println(n.getText());

            // checks if yes node is an answer
            if (n.getYes() != null && n.getYes().isAnswer()) {
                UI.print("yes:");
                printSubTree(n.getYes());
            } //else /*if (n.getYes() != null)*/ { printSubTree(n.getYes());}

            // checks if no node is an answer
            if (n.getNo() != null && n.getNo().isAnswer()) {
                UI.print("no:");
                printSubTree(n.getNo());
            } //else /*if (n.getNo() != null)*/ { printSubTree(n.getYes());}
        }
    }

    /**
     * Run the tree by starting at the top (of theTree), and working
     * down the tree until it gets to a leaf node (a node with no children)
     * If the node is a leaf it prints the answer in the node
     * If the node is not a leaf node, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     */
    public void runTree() {
        /*# YOUR CODE HERE */
        DTNode n = theTree;
        while (n != null) {
            if (!n.isAnswer()) {
                String ans = UI.askString("Is it true: " + n.getText() + " (yes/no): ");
                if (ans.toLowerCase().equals("yes")) {
                    n = n.getYes();
                } else if (ans.toLowerCase().equals("no")) {
                    n = n.getNo();
                } else { UI.println("Sorry, answer invalid."); }
            } else if (n.isAnswer()) {
                UI.println("The animal is: " + n.getText());
                return;
            }
        }
    }

    /**
     * Grow the tree by allowing the user to extend the tree.
     * Like runTree, it starts at the top (of theTree), and works its way down the tree
     *  until it finally gets to a leaf node. 
     * If the current node has a question, then it asks the question in the node,
     * and depending on the answer, goes to the "yes" child or the "no" child.
     * If the current node is a leaf it prints the decision, and asks if it is right.
     * If it was wrong, it
     *  - asks the user what the decision should have been,
     *  - asks for a question to distinguish the right decision from the wrong one
     *  - changes the text in the node to be the question
     *  - adds two new children (leaf nodes) to the node with the two decisions.
     */
    public void growTree () {
        /*# YOUR CODE HERE */
        DTNode n = theTree;
        while (n != null) {
            if (!n.isAnswer()) {
                String ans = UI.askString("Is it true: " + n.getText() + " (yes/no): ");
                if (ans.toLowerCase().equals("yes")) {
                    n = n.getYes();
                } else if (ans.toLowerCase().equals("no")) {
                    n = n.getNo();
                } else { UI.println("Sorry, answer invalid."); }
            } else if (n.isAnswer()) {
                String ans = UI.askString("I think I know. Is the animal a " + n.getText() + "?");
                if (ans.toLowerCase().equals("yes")) {
                    UI.println("Amazing!!");
                } else if (ans.toLowerCase().equals("no")) {
                    String newNode = UI.askString("Okay, what animal is it?");
                    UI.println("Oh. I can't distinguish a " + n.getText() + " from a " + newNode);
                    String property = UI.askString("Tell me something that's true for a " + newNode + " but not for a " + n.getText());
                    
                    // replace guess
                    String memory = n.getText(); // memorises guess
                    DTNode newQuestion = new DTNode(property);
                    n = newQuestion; // set guess as new question
                    
                    // add new children
                    DTNode newYes = new DTNode(newNode);
                    DTNode newNo = new DTNode(memory);
                    newQuestion.setChildren(newYes, newNo);
                    
                    UI.println("Thank you! I've updated my decision tree.");
                    /** currently doesn't actually save :( */
                } else { UI.println("Sorry, answer invalid.");}
                return;
            }
        }

    }

    // You will need to define methods for the Completion and Challenge parts.

    // Written for you

    /** 
     * Loads a decision tree from a file.
     * Each line starts with either "Question:" or "Answer:" and is followed by the text
     * Calls a recursive method to load the tree and return the root node,
     *  and assigns this node to theTree.
     */
    public void loadTree (String filename) { 
        if (!Files.exists(Path.of(filename))){
            UI.println("No such file: "+filename);
            return;
        }
        try{theTree = loadSubTree(new ArrayDeque<String>(Files.readAllLines(Path.of(filename))));}
        catch(IOException e){UI.println("File reading failed: " + e);}
    }

    /**
     * Loads a tree (or subtree) from a Scanner and returns the root.
     * The first line has the text for the root node of the tree (or subtree)
     * It should make the node, and 
     *   if the first line starts with "Question:", it loads two subtrees (yes, and no)
     *    from the scanner and add them as the  children of the node,
     * Finally, it should return the  node.
     */
    public DTNode loadSubTree(Queue<String> lines){
        Scanner line = new Scanner(lines.poll());
        String type = line.next();
        String text = line.nextLine().trim();
        DTNode node = new DTNode(text);
        if (type.equals("Question:")){
            DTNode yesCh = loadSubTree(lines);
            DTNode noCh = loadSubTree(lines);
            node.setChildren(yesCh, noCh);
        }
        return node;

    }

}
