// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 1
 * Name: Kanya Farley
 * Username: farleykany
 * ID:
 * Version: 16/7
 */

import ecs100.*;
import java.util.*;
import java.util.Stack;
import java.awt.Color;
import javax.swing.JColorChooser;

/** Pencil   */
public class Pencil{
    private static final Color DEFAULT_COLOR = Color.BLACK;
    private static final double DEFAULT_WIDTH = 3;
    
    /* stroke variables */
    private double lastX;
    private double lastY;
    private double finalX;
    private double finalY;
    private Color color = DEFAULT_COLOR;
    private double width = DEFAULT_WIDTH;
    
    /* handling strokes */
    Stroke lastStroke;
    private ArrayList<Stroke> currentStrokes = new ArrayList<>();
    
    /* handling undo/redo */
    private Stack<ArrayList<Stroke>> undoStack = new Stack<>();
    private Stack<ArrayList<Stroke>> redoStack = new Stack<>();
    
    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
        UI.addButton("Change Colour", this::changeColor);
        UI.addSlider("Pen Width", 0.5, 10, DEFAULT_WIDTH, this::setSize);
        UI.addButton("Quit", UI::quit);
        UI.setLineWidth(3);
        UI.setDivider(0.0);
    }

    /**
     * Respond to mouse events
     */
    public void doMouse(String action, double x, double y) {
        if (action.equals("pressed")){
            lastX = x;
            lastY = y;
        }
        else if (action.equals("dragged")){
            UI.setColor(color);
            UI.setLineWidth(width);
            UI.drawLine(lastX, lastY, x, y);
            lastStroke = new Stroke (lastX, lastY, x, y, color, width);
            currentStrokes.add(lastStroke);
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            ArrayList<Stroke> copy = new ArrayList<Stroke>(currentStrokes);
            addUndo(copy);
        }
    }

    /** Adds most recent stroke to undo stack, clears current stroke */
    private void addUndo(ArrayList copy) {
        undoStack.push(copy);
        currentStrokes.clear();
    }

    /** 
     * Checks if stack is empty to validate undo, erases whole stroke,
     * adds to redo stack, removes from undo stack
     */
    private void undo() {
        if (undoStack.isEmpty()) {
            UI.println("No strokes to undo");
        } else {
            for (int i = 0; i < undoStack.peek().size(); i++) { 
                Stroke toErase = undoStack.peek().get(i);
                toErase.erase();
            }
            redoStack.push(undoStack.peek());
            undoStack.pop();
        }
    }

    /**
     * Checks if stack is empty to validate redo, draws last undone stroke,
     * repopulates current stroke, adds to undo stack, removes from redo stack
     */
    private void redo() {
        if (redoStack.isEmpty()) {
            UI.println("No actions to redo.");
        } else {
            for (int i = 0; i < redoStack.peek().size(); i++) {
                Stroke toRedraw = redoStack.peek().get(i);
                toRedraw.draw();
                currentStrokes.add(redoStack.peek().get(i));
            }
            undoStack.push(redoStack.peek());
            redoStack.pop();
        }
    }
    
    private void changeColor() {
        color = JColorChooser.showDialog(null, "Choose Colour", DEFAULT_COLOR);
    }
    
    private void setSize(double width) {
        this.width = width;
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }
}
