
/**
 * Class for stroke objects
 *
 * @author Kanya Farley
 * @version 15/7
 */
import ecs100.*;
public class Stroke
{
    // instance variables
    private double firstX;
    private double firstY;
    private double lastX;
    private double lastY;

    /**
     * Constructor for objects of class Stroke
     */
    public Stroke(double firstX, double firstY, double lastX, double lastY)
    {
        this.firstX = firstX;
        this.firstY = firstY;
        this.lastX = lastX;
        this.lastY = lastY;
    }

    /* Getters */
    public double getFirstX() {return(firstX);}

    public double getFirstY() {return(firstY);}

    public double getLastX() {return(lastX);}

    public double getLastY() {return(lastY);}

    /**
     * Draws existing stroke
     */
    public void draw() {{UI.drawLine(this.getFirstX(), this.getFirstY(), this.getLastX(), this.getLastY());}}

    /**
     * Erases existing stroke
     */
    public void erase() {UI.eraseLine(this.getFirstX(), this.getFirstY(), this.getLastX(), this.getLastY());}
}