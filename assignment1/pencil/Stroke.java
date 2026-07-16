
/**
 * Class for stroke objects
 *
 * @author Kanya Farley
 * @version 16/7
 */
import ecs100.*;
import java.awt.Color;
public class Stroke
{
    // instance variables
    private double firstX;
    private double firstY;
    private double lastX;
    private double lastY;
    private Color color;
    private double width;
    /**
     * Constructor for objects of class Stroke
     */
    public Stroke(double firstX, double firstY, double lastX, double lastY, Color color, double width)
    {
        this.firstX = firstX;
        this.firstY = firstY;
        this.lastX = lastX;
        this.lastY = lastY;
        this.color = color;
        this.width = width;
    }

    /* Getters */
    public double getFirstX() {return(firstX);}

    public double getFirstY() {return(firstY);}

    public double getLastX() {return(lastX);}

    public double getLastY() {return(lastY);}
    
    public Color getColor() {return(color);}
    
    public double getWidth() {return(width);}

    /**
     * Draws existing stroke
     */
    public void draw() {
        UI.setColor(color);
        UI.setLineWidth(width);
        UI.drawLine(this.getFirstX(), this.getFirstY(), this.getLastX(), this.getLastY());
    }

    /**
     * Erases existing stroke
     */
    public void erase() {
        UI.setLineWidth(width);
        UI.eraseLine(this.getFirstX(), this.getFirstY(), this.getLastX(), this.getLastY());
    }
}