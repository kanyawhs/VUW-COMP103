// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 2
 * Name: Kanya Farley
 * Username: farleykany
 * ID:
 * Version: 26/7
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
        earthquakes.sort((e1, e2) -> {
            Earthquake earthquakeA = earthquakes.get(e1);
            Earthquake earthquakeB = earthquakes.get(e2);
            double magnitude1 = earthquakeA.getMagnitude();
            double magnitude2 = earthquakeB.getMagnitude();
            double result = magnitude1 - magnitude2;
            return(result);
        });

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
            Earthquake earthquakeA = earthquakes.get(e1);
            Earthquake earthquakeB = earthquakes.get(e2);
            int year1 = earthquakeA.getYear();
            int year2 = earthquakeB.getYear();
            if (year1 != year2) {
                return year1-year2;
            } else { // if years are same
                int month1 = earthquakeA.getMonth();
                int month2 = earthquakeA.getMonth();
                if (month1 != month2) { // if year and month and day is the same
                    return month1-month2;
                } else {
                    int day1 = earthquakeA.getDay();
                    int day2 = earthquakeA.getDay();
                    if (day1 != day2) {
                        return day1-day2;
                    } else {
                        
                    }
                }
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
