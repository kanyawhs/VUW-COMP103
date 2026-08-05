// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/**
 * Issue: when listing lines of station or station of lines, they do not actually contain any?? only in methods
 */

/* Code for COMP103 - 2026T2, Assignment 2
 * Name: Kanya Farley
 * Username: farleykany
 * ID:
 * Version: 5/8
 */

import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    /*# YOUR CODE HERE */
    Map<String, Station> stations = new HashMap<String, Station>(); // key is name, value is station
    Map<String, TrainLine> trainLines = new HashMap<String, TrainLine>();

    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;
    private int startTime = 0;         // time for enquiring about

    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    private static boolean loadedData = false;  // used to ensure that the data have been loaded.

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        // The following is only needed for the Completion and Challenge
        loadTrainServicesData();
        UI.println("Loaded Train Services");
        loadedData = true;
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
                {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextServices(this.stationName, this.startTime);});
        //UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        // this is just to remind you to start the program using main!
        if (! loadedData){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    public void doMouse(String action, double x, double y){
        if (action.equals("released")){
            /*# YOUR CODE HERE */

        }
    }

    // Methods for loading data and answering queries

    /*# YOUR CODE HERE */
    /* stations */
    /**
     * Checks for each station parameter in file then creates a station and adds to map
     */
    public void loadStationData() {
        File stationData = new File ("data/stations.data");
        try {
            Scanner sc = new Scanner(stationData);
            while (sc.hasNext()) {
                String name = sc.next();
                int zone = sc.nextInt();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                Station temp = new Station(name, zone, x, y);
                stations.put(name, temp);
            }
        } catch (IOException e){UI.println("Error: File not found");}
    }

    /**
     * Lists stations in station HashMap as String
     */
    public void listAllStations() {
        UI.println("All stations in region:");
        for (Station listedStation: stations.values()) {
            UI.println(listedStation.toString());
        }
    }

    /**
     * Converts current stations HashMap to TreeMap for alphabetical order then prints
     */
    public void listStationsByName() {
        UI.println("All stations (in alphabetical order):");
        Map<String, Station> treeVer = new TreeMap<>(stations);
        for (String station: treeVer.keySet()) {
            UI.println(station);
        }
    }

    /**
     * Checks if stationName is in list of stations, fetches all of its train lines
     */
    public void listLinesOfStation(String stationName) {
        if (!stations.keySet().contains(stationName)) {
            UI.println("Please enter an existing station");
        } else {
            UI.println("Train lines that go through station " + stationName + ":");
            for (TrainLine line: stations.get(stationName).getTrainLines()) {
                UI.println(line.getName());
            }
        }
    }

    /* train lines */
    /**
     * Checks file for train line name, then loads file containing its stations
     */
    public void loadTrainLineData() {
        File trainData = new File ("data/train-lines.data");
        try {
            Scanner main = new Scanner(trainData);
            while (main.hasNext()) {
                String name = main.next();
                TrainLine line = new TrainLine(name);
                trainLines.put(name, line);

                File trainLineStations = new File("data/" + name + "-stations.data");
                Scanner stationSc = new Scanner(trainLineStations);
                while (stationSc.hasNext()) {
                    String toFetch = stationSc.next();
                    if (stations.containsKey(toFetch)) {
                        line.addStation(stations.get(toFetch)); // adds station to train line
                        stations.get(toFetch).addTrainLine(line); // adds same train line to same station
                    }
                }
            }
        } catch (IOException e){UI.println("Error: File not found");}
    }

    /**
     * Lists train lines in trainLines HashMap
     */
    public void listAllTrainLines() {
        UI.println("All train lines in region:");
        UI.println(trainLines.size());
        for (TrainLine listedLine: trainLines.values()) {
            UI.println(listedLine.toString());
        }
    }

    /**
     * Checks if train line exists, then lists its respective stations
     */
    public void listStationsOnLine(String lineName) {
        if (!trainLines.keySet().contains(lineName)) {
            UI.println("Please enter an existing train line");
        } else {
            UI.println("Stations on line " + lineName + ":");
            for (int i = 0; i < trainLines.get(lineName).getStations().size(); i++) {
                UI.println(trainLines.get(lineName).getStations().get(i));
            }
        }
    }

    /**
     * Takes given station 1 and station 2 @param
     * Checks through all train lines to find if there's a connected train line
     */
    public boolean checkConnected(String stationName, String destinationName) {
        for (String line: trainLines.keySet()) {
            if (line.equals(stationName + "_" + destinationName)) {
                UI.println("Stations are connected on " + line);
                return true;
            } else {
                UI.println("Station is not connected to " + destinationName);
            }
        }
        return false;
    }

    public void loadTrainServicesData() {
        for (String listedLine: trainLines.keySet()) {
            File serviceData = new File ("data/" + listedLine + "-services.data");
            TrainService newService = new TrainService(trainLines.get(listedLine));
            try {
                Scanner sc = new Scanner(serviceData);
                while (sc.hasNext()) {
                    int time = sc.nextInt();
                    newService.addTime(time);
                }
                /* debugging
                UI.println("Times for " + newService.getTrainID() + ": ");
                for (int time: newService.getTimes()) {
                UI.println(time);
                }*/
            } catch (IOException e) {UI.println("Error: file not found"); }

        }
    }

    public void findNextServices(String stationToGet, int time) {
        if (!stations.containsKey(stationToGet)) {
            UI.println("Station name invalid. Please try again.");
        } else { 
            UI.println("Next trips after " + time + " for " + stationToGet + ":");
            Station station = stations.get(stationToGet);
            for (TrainLine line : station.getTrainLines()) {
                UI.println("test1");
                for (TrainService service : line.getTrainServices()) { // not accessed
                    UI.println("test2");
                    for (Integer times : service.getTimes()) { // not accessed
                        UI.println("test3");
                        if (times > time) {
                            UI.println("Next service for " + line + ": " + times);
                            break;
                        }
                    }
                }
            }
        }
    }
}
