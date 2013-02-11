/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/* 
 * Ground Zero Template
 * This Template is to be utilized to troubleshoot the robot. Please make no
 * changes to this code. It's to be copied and branched off of for new projects
 * and is for reference only.
 * Are you trying to install netbeans? For the love of God, read:
 * !http://first.wpi.edu/Images/CMS/First/Getting_Started_with_Java_for_FRC.pdf
 * http://wpilib.screenstepslive.com/s/3120/m/7885 Revision 2013
 * 
 * TLDR:
 **Install NetBeans with Sun JDK and update completely
 **NetBeans->Tools->Plugin->Settings->Add:
 ***http://first.wpi.edu/FRC/java/netbeans/update/updates.xml
 **NetBeans->Tools->Plugin->Available->Check all that say FRC
 **Check for updates
 **NetBeans->Tools->Options->Misc->FRC->Team# 3734
 * 
 * 
 * You said I could comment anything, So I commented a comment:
 * http://xkcd.com/156/
 * -Ben Shaughnessy
*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class AwesomOSimple extends SimpleRobot {
    
    // Attach digital sidecar to 4 Jaguar PWMs. Inputs expect (left,right) 
    // and have further ability to put in for 4 motor drive, listed as
    // (left,right,left 2, right 2). Further Jaguars can be added with the
    // jaguar declaration, but by default we are not using the jaguar
    // package.
    RobotDrive drive = new RobotDrive(5,7,6,8);
    
    // XBox Controller is treated exactly the same as a regular joystick with
    // 7 axes (only 6 available)
    /************************************************************************
     * XBox Controller Mappings
     * 
     * 1: A
     * 2: B
     * 3: X
     * 4: Y
     * 5: Left Bumper
     * 6: Right Bumper
     * 7: Back
     * 8: Start
     * 9: Left Joystick
     * 10: Right Joystick
     * 
     * Axis Mappings getRawAxis(1-6)
     * (all output is between -1 and 1)
     * 1: Left Stick X Axis     (Left:Negative ; Right: Positive)
     * 2: Left Stick Y Axis     (Up: Negative ; Down: Positive)
     * 3: Triggers: Left        (Positive ; Right: Negative)
     * 4: Right Stick X Axis    (Left: Negative ; Right: Positive)
     * 5: Right Stick Y Axis    (Up: Negative ; Down: Positive)
     * 6: Directional Pad       (Not recommended, buggy)
     ***********************************************************************/
    Joystick xboxControl = new Joystick(1);
    
    // Relays have to be announced prior to initialization and then must be
    // attached to a sidecar pin from within robotInit()
    Relay firingMechanism;
    Talon frisbeeIndexer;
    
    // Boolean used to determine whether or not the frisby indexer is on
    boolean indexerActive = false;
            
    /* Timestamp variables for identifying the robots current cycle time
     * This is to be used most primarily for user intarction via button
     * presses. Prevents the button from being repeatedly cycled.
     */
    long CPUlastTime; long CPUcurrentTime; long CPUtimeDifference;
    
    // drivingStyleModifier decides if robot is running in arcade mode or
    // tank drive. (0 default; arcade mode).
    int drivingStyleModifier = 0;
    
    /**
     * This method is called once each time AwesomO is enabled for both 
     * autonomous and tele-operated modes.
     */
    public void robotInit() {
        /* Attach our predefined motors to a point on our digital sidecar
         * for some reason, this must be done in Init seperately of the 
         * declaration above, instead of being done prior.
         * 
         * Available declaration options: 
         * Relay(); Solenoid(); Talon(); Jaguar(); probably others.
         * 
         * Syntax: Object = new declarationOption(sidecarNumber);
         * 
         * Conditions: Requires object to have been declared prior to this
         * robotInit() method. (i.e. Relay exampleRelay;)
         * 
         */
        firingMechanism = new Relay(1);
        frisbeeIndexer = new Talon(9);
        
        //camera -- this is not yet properly attached to the access camera
        //method. This needs to be resolved all within callAxisCamera().
        AxisCamera camera;
        camera = AxisCamera.getInstance();
        camera.writeResolution(AxisCamera.ResolutionT.k320x240);
        camera.writeBrightness(50);
        //^---fix meeee---^
    }
    
    /**
     * This function is called once each time the robot enters autonomous mode.
     */
    public void autonomous() {
        /*getWatchdog().setEnabled(true);
        getWatchdog().setExpiration(0.5);
        drive.drive(1,0);
        Timer.delay(2);
        drive.drive(0,0);*/
        // Let's get a sonar sensor as well as a directional sensor on this 
        // bad boy and get the robot patrolling the halls. So unnerving!
        for (int i = 0; i < 4; i++) {
            drive.drive(0.5, 0.0); // drive 50% fwd 0% turn
            Timer.delay(2.0); // wait 2 seconds
            drive.drive(0.0, 0.75); // drive 0% fwd 0% turn
        }
    }

    /**
     * This function is called once each time the robot enters operator control.
     */
    public void operatorControl() {
        while (true && isOperatorControl() && isEnabled()) // loop until change
        {
            //tank drive OR arcade drive available
            //if (drivingStyleModifier == 1) {
                drive.arcadeDrive(xboxControl.getY(), xboxControl.getX());
            //}
            /*else {
                drive.tankDrive(xboxControl.getY(), xboxControl.getRawAxis(5)); // drive w/ joysticks
            }*/
            
            //Toggling the indexer on and off using timestamps
            //because delays are over-rated and innefficient
            if(xboxControl.getRawButton(1)){
                
                // I want all of this moved to a sepearate function that can be
                // called by other buttonPresses. Function listed below as
                // humanInteractionHandler().
                CPUcurrentTime = System.currentTimeMillis();
                CPUtimeDifference = CPUcurrentTime - CPUlastTime;
                if(indexerActive == true) {
                    if(CPUtimeDifference > 500) {
                        indexerActive = false;
                        CPUlastTime = System.currentTimeMillis();
                    }
                }
                else {
                    if(CPUtimeDifference > 500) {
                        indexerActive = true;
                        CPUlastTime = System.currentTimeMillis();
                    }
                }
            }
            
            //Controlling the Motor
            if(indexerActive == true) {
                //Turn on the Indexer with full power
                frisbeeIndexer.set(1);
            }
            
            callAxisCamera(); //Call the webcam function
            
            /* driverstationLCD
            * sends text data to the LCD. Unsure how it refers to the camera 
            * indirectly as no visible variable is passed.
            */
           DriverStationLCD.getInstance().updateLCD();
            
            Timer.delay(0.005);
        }
    }
    
    public void callAxisCamera() {      
        /*--Webcam function--
         * This sets up all necessary variables for the webcam and updates the
         * drivers station as necessary.
         * 
         * writeCompression() 
         * Expects an int (0 least-100 most compressed) which
         * compresses image during transfer to the driver station. 0 default.
         * 
         * writeResolution(AxisCamera.ResolutionT.var)
         * Resolutions: k160x120; k320x240; k640x360; k640x480;width(int);
         * height(int); default (k320x240)
         * 
         * writeBrightness()
         * writes the brightness to use. Should be an int (0 dim - 100 bright).
         * default 50.
         */
        AxisCamera.getInstance().writeCompression(0);
        AxisCamera.getInstance().writeResolution(AxisCamera.ResolutionT.k320x240);
        AxisCamera.getInstance().writeBrightness(50);
    }
    
    public void humanInteractionHandler() {
        /*--HID monitor--
         * This method is in place to ensure that whenever a human interaction
         * occurs (most predominantly via button presses on joysticks), it will
         * ensure the process isn't repeated once every clock cycle.
         */
    }
}
