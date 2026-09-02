// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2026T2, Assignment 3
 * Name: Kanya Farley
 * Username: farleykany
 * ID: 300693857
 * Version: 2/9
 */

import ecs100.*;
import java.util.*;
import java.io.*;

/**
 * Simulation of a Hospital ER
 * 
 * The hospital has a collection of Departments, including the ER department, each of which has
 *  and a treatment room.
 * 
 * When patients arrive at the hospital, they are immediately assessed by the
 *  triage team who determine the priority of the patient and (unrealistically) a sequence of treatments 
 *  that the patient will need.
 *
 * The simulation should move patients through the departments for each of the required treatments,
 * finally discharging patients when they have completed their final treatment.
 *
 *  READ THE ASSIGNMENT PAGE!
 */

public class HospitalERCompl{

    /**
     * The map of the departments.
     * The names of the departments should be "ER", "X-Ray", "MRI", "UltraSound" and "Surgery"
     * The maximum patients should be 8 for "ER", 3 for "X-Ray", 1 for "MRI", 2 for "UltraSound" and
     *     3 for "Surgery"
     */

    private Map<String, Department> departments = new HashMap<String, Department>();

    // Copy the code from HospitalERCore and then modify/extend to handle multiple departments

    // fields for the statistics
    /*# YOUR CODE HERE */
    private int totalPatientsTreated = 0;
    private int totalWaitingTime = 0; // necessary for average wait time stat
    private int averageWaitingTime = 0;

    private int totalPriority1PatientsTreated = 0;
    private int totalPriority1WaitingTime = 0;

    // Fields for the simulation
    private boolean running = false;
    private int time = 0; // The simulated time - the current "tick"
    private int delay = 300;  // milliseconds of real time for each tick

    /**
     * stop any running simulation
     * Define the departments available and put them in the map of departments.
     * Each department needs to have a name and a maximum number of patients that
     * it can be treating at the same time.
     * reset the statistics
     */
    public void reset(boolean usePriorityQueues){
        /*# YOUR CODE HERE */
        running=false;
        UI.sleep(2*delay);  // to make sure that any running simulation has stopped

        time = 0;           // set the "tick" to zero.
        // reset the waiting room, the treatment room, and the statistics.
        /*# YOUR CODE HERE */
        departments.clear();     

        Department er = new Department("ER", 8, usePriorityQueues);
        departments.put("ER", er);

        Department xRay = new Department("X-Ray", 3, usePriorityQueues);
        departments.put("X-Ray", xRay);

        Department mri = new Department("MRI", 1, usePriorityQueues);
        departments.put("MRI", mri);

        Department ultraSound = new Department("UltraSound", 2, usePriorityQueues);
        departments.put("UltraSound", ultraSound);

        Department surgery = new Department("Surgery", 3, usePriorityQueues);
        departments.put("Surgery", surgery);        

        UI.clearGraphics();
        UI.clearText();
    }

    /**
     * Main loop of the simulation
     */
    public void run(){
        if (running) { return; } // don't start simulation if already running one!
        running = true;
        while (running){
            /*# YOUR CODE HERE */
            time++;

            // treatment room handling
            List<Patient> toMove = new ArrayList<>();
            for (Department d : departments.values()) {
                List<Patient> toRemove = new ArrayList<>();
                for (Patient p : d.getTreatingPatients()) {
                    p.advanceCurrentTreatmentByTick();
                    if (p.currentTreatmentFinished()) {toRemove.add(p);} // patient prepared to be removed once treatment finished
                }
                for (int i = 0; i < toRemove.size(); i++) {d.getTreatingPatients().remove(toRemove.get(i));}

                for (Patient p : toRemove) {
                    p.removeCurrentTreatment();
                    if (!p.allTreatmentsCompleted()) {
                        Department next = departments.get(p.getCurrentDepartment());
                        next.addPatient(p);
                    }

                    // statistics update
                    totalPatientsTreated++;
                    totalWaitingTime += p.getTotalWaitingTime();
                    averageWaitingTime = totalWaitingTime / totalPatientsTreated;

                    if (p.getPriority() == 1) {
                        totalPriority1PatientsTreated++;
                        totalPriority1WaitingTime += p.getTotalWaitingTime();
                    }

                    UI.println(time + ": Discharge: " + p);
                }
                toRemove.clear();
            }
            
            // waiting room handling
            for (Department d : departments.values()) {
                for (Patient p: d.getWaitingPatients()) {p.waitForATick();}
                while (d.getWaitingPatients().size() < d.getMaxPatients() && !d.getWaitingPatients().isEmpty()) {
                    d.getTreatingPatients().add(d.getWaitingPatients().peek());
                    UI.println(time + ": Treating: " + d.getWaitingPatients().poll());
                }
            }
            
            // Gets any new patient that has arrived and adds them to the waiting room
            Patient newPatient = PatientGenerator.getNextPatient(time);
            if (newPatient != null){
                UI.println(time+ ": Arrived: "+newPatient);
                Department first = departments.get(newPatient.getCurrentDepartment());
                first.getWaitingPatients().offer(newPatient);
            }
        }
        // paused, so report current statistics
        reportStatistics();
    }

    /**
     * Report that a patient has been discharged, along with any
     * useful statistics about the patient
     */
    public void discharge(Patient p){
        /*# YOUR CODE HERE */

    }

    /**
     * Report summary statistics about the simulation
     */
    public void reportStatistics(){
        /*# YOUR CODE HERE */

    }

    // METHODS FOR THE GUI AND VISUALISATION
    /**
     * Set up the GUI: buttons to control simulation and sliders for setting parameters
     */
    public void setupGUI(){
        UI.addButton("Reset (Queue)", () -> {this.reset(false); });
        UI.addButton("Reset (Pri Queue)", () -> {this.reset(true);});
        UI.addButton("Start", ()->{if (!running){ run(); }});   //don't start if already running!
        UI.addButton("Pause & Report", ()->{running=false;});
        UI.addSlider("Speed", 1, 400, (401-delay),
            (double val)-> {delay = (int)(401-val);});
        UI.addSlider("Av arrival interval", 1, 50, PatientGenerator.getArrivalInterval(),
            PatientGenerator::setArrivalInterval);
        UI.addSlider("Prob of Pri 1", 1, 100, PatientGenerator.getProbPri1(),
            PatientGenerator::setProbPri1);
        UI.addSlider("Prob of Pri 2", 1, 100, PatientGenerator.getProbPri2(),
            PatientGenerator::setProbPri2);
        UI.addButton("Quit", UI::quit);
        UI.setWindowSize(1000,600);
        UI.setDivider(0.5);
    }

    /**
     * Redraws all the departments
     */
    public void redraw(){
        UI.clearGraphics();
        UI.setFontSize(14);
        UI.drawString("Treating Patients", 5, 15);
        UI.drawString("Waiting Queues", 200, 15);
        UI.drawLine(0,32,400, 32);
        double y = 80;
        for (Department dept : departments.values()){
            dept.redraw(y);
            UI.drawLine(0,y+2,400, y+2);
            y += 50;
        }
    }

    /**
     * Construct a new HospitalER object, setting up the GUI, and resetting
     */
    public static void main(String[] arguments){
        HospitalERCompl er = new HospitalERCompl();
        er.setupGUI();
        er.reset(false);   // initialise with an ordinary queue.
    }        

}
