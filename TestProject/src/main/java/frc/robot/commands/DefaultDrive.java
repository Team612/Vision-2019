/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import edu.wpi.first.wpilibj.GenericHID.Hand;

public class DefaultDrive extends Command {


  final double DEADZONE = 0.15;  // Define controller deadzone

  // Variables for mechanum drive
  public static double magnitude;  // The power the of the drive system
  public static double angle;  // The translation angle relative to the robot's body vector
  public static double rotation;  // The rotation is the magnitude of the robot's rotation rate

  // Variables for Joystick movements 
  double direction_x;
  double direction_y; 


  public DefaultDrive() {
    requires(Robot.drivetrain);  // Require the drivetrain object
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
  }


  protected void getInput() {  // Fetch the Joystick values
    direction_y = OI.driver.getY(Hand.kLeft);
    direction_x = OI.driver.getX(Hand.kLeft);
    rotation    = OI.driver.getX(Hand.kRight);
  }

  // Called repeatedly when this Command is scheduled to run
  protected  void doDead() {  // Calculates the DEADZONE

    if(Math.abs(direction_x) < DEADZONE){
        direction_x = 0;
    }
    if(Math.abs(direction_y) < DEADZONE){
        direction_y = 0;
    }
    if(Math.abs(rotation) < DEADZONE){
        rotation = 0;
    }

  }


  protected void toPolar() {  // Calculate the Magnitude & Angle of the Mechanum drive
    magnitude = Math.sqrt((direction_x * direction_x) + (direction_y * direction_y)); // sqrt(X^2 + Y^2)
    if (magnitude > 1.0) {  // If Magnitude over 1 set it to 1
        magnitude = 1.0;
    }
    angle = Math.atan2(direction_y, direction_x) * (180 / Math.PI);  // arctan(y/x) Calculates the angle of the y and x point then converts to radians
  }


  @Override
  protected void execute() {
    getInput(); // Fetches Joystick values
    doDead(); // Sets the DEADZONE value
    toPolar(); // Does calculations with Joystick values to drivetrain
    Robot.drivetrain.drivetrain.drivePolar(magnitude, angle, rotation); // Pass the calculated drive data into the drivetrain
  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return false;
  }

  // Called once after isFinished returns true
  @Override
  protected void end() {
  }

  // Called when another command which requires one or more of the same
  // subsystems is scheduled to run
  @Override
  protected void interrupted() {
  }
}
