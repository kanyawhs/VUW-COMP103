// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 2
 * Name: Kanya Farley
 * Username: farleykany
 * ID:
 * Version: 7/8
 */

import ecs100.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * EarthquakeSorter
 * Sorts data about a collection of 4335 NZ earthquakes from May 2016 to May 2017
 * Each line of the file "earthquake-data.txt" has a description of one earthquake:
 *   ID time longitude latitude magnitude depth region
 * Data is from http://quakesearch.geonet.org.nz/
 *  Note the earthquakes' ID have been modified to suit this assignment.
 *  Note bigearthquake-data.txt has just the 421 earthquakes of magnitude 4.0 and above
 *   which may be useful for testing, since it is not as big as the full file.
 *   
 */

public class EarthquakeSorter{

    private List<Earthquake> earthquakes = new ArrayList<Earthquake>();

    /**
     * Load data from the specified data file into the earthquakes field:
     */
    public void loadData(String filename){
        try {
            /*# YOUR CODE HERE */
            Scanner read = new Scanner(Path.of(filename));
            while (read.hasNext()) {
                String ID = read.next();
                String date = read.next();
                String time = read.next();
                double longitude = read.nextDouble();
                double latitude = read.nextDouble();
                float magnitude = read.nextFloat();
                float depth = read.nextFloat();
                String region = read.next();

                Earthquake temp = new Earthquake(ID, date, time, longitude, latitude, magnitude, depth, region);
                earthquakes.add(temp);
                temp = null;
            }
            UI.printf("Loaded %d earthquakes into list\n", this.earthquakes.size());
            UI.println("----------------------------");
        } catch(IOException e){UI.println("File reading failed");}
    }

    /**
     * Sorts the earthquakes by ID
     */
    public void sortByID(){
        UI.clearText();
        UI.println("Earthquakes sorted by ID");
        /*# YOUR CODE HERE */
        Collections.sort(earthquakes);

        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by magnitude, largest first
     * 
     * Plan:
     * - get magnitude
     * - sort it??
     */
    public void sortByMagnitude(){
        UI.clearText();
        UI.println("Earthquakes sorted by magnitude (largest first)");
        /*# YOUR CODE HERE */
        earthquakes.sort((e1, e2) -> Double.compare(e2.getMagnitude(), e1.getMagnitude()));

        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to the date and time that they occurred.
     */
    public void sortByTime(){
        UI.clearText();
        UI.println("Earthquakes sorted by time");
        /*# YOUR CODE HERE */
        earthquakes.sort((e1, e2) -> {
            // converts dates to int format
            String[] date1 = e1.getDate().split("-");
            String[] date2 = e2.getDate().split("-");
            String date1v2 = String.join(" ", date1);
            String date2v2 = String.join(" ", date2);
            // remove whitespace
            String date1v3 = date1v2.replaceAll("\\s", "");
            String date2v3 = date2v2.replaceAll("\\s", "");
            // parse as double
            int date = Integer.parseInt(date1v3);
            int comparingDate = Integer.parseInt(date2v3);
            if (date != comparingDate) {
                return (date - comparingDate);
            } else {
                // converts times to double format
                String[] time1 = e1.getTime().split(":");
                String[] time2 = e2.getTime().split(":");
                String time1v2 = String.join(" ", time1);
                String time2v2 = String.join(" ", time2);
                // remove whitespace
                String time1v3 = time1v2.replaceAll("\\s", "");
                String time2v3 = time2v2.replaceAll("\\s", "");
                // parse as double
                double time = Double.parseDouble(time1v3);
                double comparingTime = Double.parseDouble(time2v3);
                return Double.compare(time, comparingTime);
            }
        });
        
        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the list of earthquakes according to region. If two earthquakes have the same
     *   region, they should be sorted by magnitude (highest first) and then depth (more shallow first)
     */
    public void sortByRegion(){
        UI.clearText();
        UI.println("Earthquakes sorted by region, then by magnitude and depth");
        /*# YOUR CODE HERE */
        earthquakes.sort((e1, e2) -> {
            String reg1 = e1.getRegion();
            String reg2 = e2.getRegion();
            if (!reg1.equals(reg2)) {
                return(reg1.compareTo(reg2));
            } else if (e1.getMagnitude() != e2.getMagnitude()) {
                return(Double.compare(e2.getMagnitude(), e1.getMagnitude()));
            } else {
                return(Double.compare(e1.getDepth(), e2.getDepth())); 
            }
        });
        for (Earthquake e : this.earthquakes){
            UI.println(e);
        }
        UI.println("------------------------");
    }

    /**
     * Sorts the earthquakes by proximity to a specified location
     * If two earthquakes are at the same distance, sort them by
     * decreasing magnitude. Print each earthquake with its distance
     * to the location.     
     */
    public void sortByProximity(double longitude, double latitude){
        UI.clearText();
        UI.println("Earthquakes sorted by proximity");
        UI.println("Longitude: " + longitude + " Latitude: " + latitude );
        /*# YOUR CODE HERE */

        UI.println("------------------------");
    }

    /**
     * Add the buttons
     */
    public void setupGUI(){
        UI.initialise();
        UI.addButton("Load", this::loadData);
        UI.addButton("sort by ID",  this::sortByID);
        UI.addButton("sort by Magnitude",  this::sortByMagnitude);
        UI.addButton("sort by Time",  this::sortByTime);
        UI.addButton("sort by Region", this::sortByRegion);
        UI.addButton("sort by Proximity", this::sortByProximity);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(900,400);
        UI.setDivider(1.0);  //text pane only 
    }

    public static void main(String[] arguments){
        EarthquakeSorter obj = new EarthquakeSorter();
        obj.setupGUI();
    }   

    public void loadData(){
        this.loadData(UIFileChooser.open("Choose data file"));
    }

    public void sortByProximity(){
        UI.clearText();
        this.sortByProximity(UI.askDouble("Give longitude: "), UI.askDouble("Give latitude: "));
    }
}
