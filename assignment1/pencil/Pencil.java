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

/** Pencil   */
public class Pencil{
    private double lastX;
    private double lastY;
    private double finalX;
    private double finalY;
    
    Stroke lastStroke;
    
    private ArrayList<Stroke> strokes = new ArrayList<>();
    
    private Stack<ArrayList<Stroke>> undoStack = new Stack<>();
    private Stack<ArrayList<Stroke>> redoStack = new Stack<>();
    /**
     * Setup the GUI
     */
    public void setupGUI(){
        UI.setMouseMotionListener(this::doMouse);
        UI.addButton("Undo", this::undo);
        UI.addButton("Redo", this::redo);
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
            UI.drawLine(lastX, lastY, x, y);
            lastStroke = new Stroke (lastX, lastY, x, y);
            strokes.add(lastStroke);
            lastX = x;
            lastY = y;
        }
        else if (action.equals("released")){
            ArrayList<Stroke> copy = new ArrayList<Stroke>(strokes);
            addUndo(copy);
        }
    }

    /** Adds most recent stroke to undo stack */
    private void addUndo(ArrayList copy) {
        undoStack.push(copy);
        strokes.clear();
    }

    /** 
     * Erases whole stroke, removes from undo stack, then adds to redo stack
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
     * Checks if stack is empty to validate redo, draws last undone stroke, removes from redo stack, adds to undo stack, repopulates stroke
     */
    private void redo() {
        if (redoStack.isEmpty()) {
            UI.println("No actions to redo.");
        } else {
            for (int i = 0; i < redoStack.peek().size(); i++) {
                Stroke toRedraw = redoStack.peek().get(i);
                toRedraw.draw();
                strokes.add(redoStack.peek().get(i));
            }
            undoStack.push(redoStack.peek());
            redoStack.pop();
        }
    }

    public static void main(String[] arguments){
        new Pencil().setupGUI();
    }
}
