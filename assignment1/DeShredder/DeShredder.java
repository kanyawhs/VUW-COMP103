// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 1
 * Name: Kanya Farley
 * Username: farleykany
 * ID: 
 * Version: 14/7
 */

import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * DeShredder allows a user to sort fragments of a shredded document ("shreds") into strips, and
 * then sort the strips into the original document.
 * The program shows
 *   - a list of all the shreds along the top of the window, 
 *   - the working strip (which the user is constructing) just below it.
 *   - the list of completed strips below the working strip.
 * The "rotate" button moves the first shred on the list to the end of the list to let the
 *  user see the shreds that have disappeared over the edge of the window.
 * The "shuffle" button reorders the shreds in the list randomly.
 * The user can use the mouse to drag shreds between the list at the top and the working strip,
 *  and move shreds around in the working strip to get them in order.
 * When the user has the working strip complete, they can move
 *  the working strip down into the list of completed strips, and reorder the completed strips.
 *
 */
public class DeShredder {

    // Fields to store the lists of Shreds and strips.  These should never be null.
    private List<Shred> allShreds = new ArrayList<Shred>();    //  List of all shreds
    private List<Shred> workingStrip = new ArrayList<Shred>(); // Current strip of shreds
    private List<List<Shred>> completedStrips = new ArrayList<List<Shred>>();

    // Constants for the display and the mouse
    public static final double LEFT = 20;       // left side of the display
    public static final double TOP_ALL = 20;    // top of list of all shreds 
    public static final double GAP = 5;         // gap between strips
    public static final double SIZE = Shred.SIZE; // size of the shreds

    public static final double TOP_WORKING = TOP_ALL+SIZE+GAP;
    public static final double TOP_STRIPS = TOP_WORKING+(SIZE+GAP);

    // Fields for recording where the mouse was pressed  (which list/strip and position in list)
    // note, the position may be past the end of the list!
    private List<Shred> fromStrip;   // The strip (List of Shreds) that the user pressed on
    private int fromPosition = -1;   // index of shred in the strip

    /**
     * Initialises the UI window, and sets up the buttons. 
     */
    public void setupGUI() {
        UI.addButton("Load library",   this::loadLibrary);
        UI.addButton("Rotate",         this::rotateList);
        UI.addButton("Shuffle",        this::shuffleList);
        UI.addButton("Complete Strip", this::completeStrip);
        UI.addButton("Quit",           UI::quit);

        UI.setMouseListener(this::doMouse);
        UI.setWindowSize(1000,800);
        UI.setDivider(0);
    }

    /**
     * Asks the user for a library of shreds, loads it, and redisplays.
     * - Uses UIFileChooser to let user select the library.
     * - Finds out how many images are in the library.
     * - Calls load(...) to construct the List of all the Shreds.
     * - Calls display().
     */
    public void loadLibrary(){
        Path filePath = Path.of(UIFileChooser.open("Choose first shred in directory"));
        Path directory = filePath.getParent(); //subPath(0, filePath.getNameCount()-1);
        int count=1;
        while(Files.exists(directory.resolve(count+".png"))){ count++; }
        //loop stops when count.png doesn't exist
        count = count-1;
        load(directory, count);   // YOU HAVE TO COMPLETE THE load METHOD
        display();
    }

    /**
     * Resets the program state by emptying out all the current lists (the list of all shreds,
     *  the working strip, and the completed strips).
     * Loads the library of shreds into the allShreds list.
     *
     * Parameters are the directory containing the shred images and the number of shreds.
     *
     * Each new Shred needs the directory and the number/id of the shred.
     */
    public void load(Path dir, int count) {
        /*# YOUR CODE HERE */
        /* clearing all ArrayLists */
        allShreds.clear();
        workingStrip.clear();
        completedStrips.clear();

        /* adds new shreds from selected folder */
        for (int i = 1; i < count; i++) {
            Shred temp = new Shred(dir, i);
            allShreds.add(temp);
        }
        display();
    }

    /**
     * Rotates the list of all shreds by one step to the left
     * and redisplays.
     * Should not throw an error if the list is empty.
     * (Called by the "Rotate" button)
     */
    public void rotateList(){
        /*# YOUR CODE HERE */
        if (allShreds.size() > 0) {
            Shred leftMostShred = allShreds.get(0); // remembers leftmost shred

            allShreds.remove(0);
            for (int i = 0; i < allShreds.size(); i++) { // erases shreds
                UI.eraseRect(LEFT + (GAP * i), TOP_ALL, SIZE, SIZE); 
            }

            allShreds.add(leftMostShred);
            for (int i = 0; i < allShreds.size(); i++) { // redraws as rotation
                allShreds.get(i).draw(LEFT + (GAP * i), TOP_ALL);
            }
        } else if (allShreds.size() <= 0) {
            UI.println("Nothing to rotate.");
        }
    }

    /**
     * Shuffles the list of all shreds into a random order
     * and redisplays.
     * (Called by the "Shuffle" button)     
     */
    public void shuffleList(){
        /*# YOUR CODE HERE */
        for (int i = 0; i < allShreds.size(); i++) {
            int randomPlace = (int)(Math.random() * (allShreds.size())); // randomizes placement
            Shred memory = allShreds.get(i);
            allShreds.remove(i);
            allShreds.add(randomPlace, memory); 
        }
    }

    /**
     * Moves the current working strip to the end of the list of completed strips
     * and redisplays.
     * (Called by the "Complete Strip" button)
     */
    public void completeStrip(){
        /*# YOUR CODE HERE */
        if (workingStrip.size() < 5 || workingStrip.size() > 5) { // boundary checks
            UI.println("Can't move to completed strip: strip size invalid.");
        } else {
            UI.eraseRect(LEFT + (GAP*5), TOP_WORKING, SIZE*5, SIZE); // erases working strip

            /* memorizes strip */
            ArrayList<Shred> completedStrip = new ArrayList<>();
            for (int i = 0; i < workingStrip.size(); i++) {
                completedStrip.add(workingStrip.get(i));
            }
            workingStrip.clear();
            completedStrips.add(completedStrip);
            display();
        }
    }

    /**
     * Handles mouse actions to move shreds and strips, and redisplays.
     * 
     * Supported user actions:
     * - Moves a Shred from allShreds to a position in the working strip.
     * - Moves a Shred from the working strip back into allShreds.
     * - Moves a Shred around within the working strip.
     * - Moves a completed Strip around within the list of completed strips.
     * - Moves a completed Strip back to become the working strip (only if the working strip is empty).
     * 
     * System behaviour:
     * - Moving a shred to a position past the end of a List puts it at the end.
     * - Attempting an invalid action has no effect.
     * 
     * Implementation rules:
     * - Determines the strip and column using getStrip(y) and getColumn(x). Do not modify these.
     * - Must delegate specific actions to helper methods rather than implementing them directly.
     */
    public void doMouse(String action, double x, double y){
        if (action.equals("pressed")){
            fromStrip = getStrip(y);      // the List of shreds to move from (possibly null)
            fromPosition = getColumn(x);  // the index of the shred to move (may be off the end)
        }
        if (action.equals("released")){
            List<Shred> toStrip = getStrip(y); // the List of shreds to move to (possibly null)
            int toPosition = getColumn(x);     // the index to move the shred to (may be off the end)
            // perform the correct action, depending on the from/to strips/positions
            /*# YOUR CODE HERE */
            if (toStrip == workingStrip && fromStrip == allShreds) { // all shreds to working strip
                addToWorkingStrip(fromPosition, toPosition, fromStrip);
            } else if (toStrip == allShreds && fromStrip == workingStrip) { // working strip to all shreds
                addToAllShreds(fromPosition, toPosition);
            } else if (toStrip == workingStrip && fromStrip == workingStrip) { // moving around working strip
                addToWorkingStrip(fromPosition, toPosition, fromStrip);
            }
            
            if (fromStrip >= TOP_STRIPS && toStrip >= TOP_STRIPS) {
                
            }
            display();
        }
    }
    // Additional methods to perform the different actions, called by doMouse

    /*# YOUR CODE HERE */ 
    /**
     * Selected working shred is removed and put into correct place in allShreds
     */
    public void addToAllShreds(int fromPosition, int toPosition) {
        Shred memory = workingStrip.get(fromPosition);
        workingStrip.remove(fromPosition);
        allShreds.add(toPosition, memory);
        display();
    }

    /**
     * Selected shred is removed and put into correct place in working strip
     */
    public void addToWorkingStrip(int fromPosition, int toPosition, List<Shred> fromStrip) {
        Shred memory = null;
        if (fromStrip == allShreds) { // remove shred from allShreds
            memory = allShreds.get(fromPosition);
            allShreds.remove(fromPosition);
        } else if (fromStrip == workingStrip) { // remove shred from workingStrip
            memory = workingStrip.get(fromPosition);
            workingStrip.remove(fromPosition);
        }

        /* place shred in working strip */
        if (memory != null && workingStrip.size() >= toPosition) { // a shred is in intended position
            workingStrip.add(toPosition, memory);
        } else if(memory != null && workingStrip.size() < toPosition) { // no shreds in intended position
            workingStrip.add(workingStrip.size(), memory);
        }
        display();
    }

    public void addToCompletedStrips(int fromPosition, int toPosition) {
        
    }
    //=============================================================================
    // Completed for you. Do not change.
    // loadImage and saveImage may be useful for the challenge.

    /**
     * Displays the remaining Shreds, the working strip, and all completed strips
     */
    public void display(){
        UI.clearGraphics();

        // list of all the remaining shreds that haven't been added to a strip
        double x=LEFT;
        for (Shred shred : allShreds){
            shred.drawWithBorder(x, TOP_ALL);
            x+=SIZE;
        }

        //working strip (the one the user is workingly working on)
        x=LEFT;
        for (Shred shred : workingStrip){
            shred.draw(x, TOP_WORKING);
            x+=SIZE;
        }
        UI.setColor(Color.red);
        UI.drawRect(LEFT-1, TOP_WORKING-1, SIZE*workingStrip.size()+2, SIZE+2);
        UI.setColor(Color.black);

        //completed strips
        double y = TOP_STRIPS;
        for (List<Shred> strip : completedStrips){
            x = LEFT;
            for (Shred shred : strip){
                shred.draw(x, y);
                x+=SIZE;
            }
            UI.drawRect(LEFT-1, y-1, SIZE*strip.size()+2, SIZE+2);
            y+=SIZE+GAP;
        }
    }

    /**
     * Returns which column the mouse position is on.
     * This will be the index in the list of the shred that the mouse is on, 
     * (or the index of the shred that the mouse would be on if the list were long enough)
     */
    public int getColumn(double x){
        return (int) ((x-LEFT)/(SIZE));
    }

    /**
     * Returns the strip that the mouse position is on.
     * This may be the list of all remaining shreds, the working strip, or
     *  one of the completed strips.
     * If it is not on any strip, then it returns null.
     */
    public List<Shred> getStrip(double y){
        int row = (int) ((y-TOP_ALL)/(SIZE+GAP));
        if (row<=0){
            return allShreds;
        }
        else if (row==1){
            return workingStrip;
        }
        else if (row-2<completedStrips.size()){
            return completedStrips.get(row-2);
        }
        else {
            return null;
        }
    }

    /**
     * Loads an image from a file and returns it as a two-dimensional array of Color.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public Color[][] loadImage(String imageFileName) {
        if (imageFileName==null || !Files.exists(Path.of(imageFileName))){
            return null;
        }
        try {
            BufferedImage img = ImageIO.read(Files.newInputStream(Path.of(imageFileName)));
            int rows = img.getHeight();
            int cols = img.getWidth();
            Color[][] ans = new Color[rows][cols];
            for (int row = 0; row < rows; row++){
                for (int col = 0; col < cols; col++){                 
                    Color c = new Color(img.getRGB(col, row));
                    ans[row][col] = c;
                }
            }
            return ans;
        } catch(IOException e){UI.println("Reading Image from "+imageFileName+" failed: "+e);}
        return null;
    }

    /**
     * Saves a 2D array of Color as an image file.
     * Maybe useful for the challenge. Not required for the core or completion.
     */
    public  void saveImage(Color[][] imageArray, String imageFileName) {
        int rows = imageArray.length;
        int cols = imageArray[0].length;
        BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Color c =imageArray[row][col];
                img.setRGB(col, row, c.getRGB());
            }
        }
        try {
            if (imageFileName==null) { return;}
            ImageIO.write(img, "png", Files.newOutputStream(Path.of(imageFileName)));
        } catch(IOException e){UI.println("Image reading failed: "+e);}

    }

    /**
     * Creates an object and set up the user interface
     */
    public static void main(String[] args) {
        DeShredder ds =new DeShredder();
        ds.setupGUI();

    }

}
