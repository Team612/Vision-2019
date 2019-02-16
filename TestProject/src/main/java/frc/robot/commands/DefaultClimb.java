/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.Climb;

public class DefaultClimb extends Command {

  private final static double CLIMB_SPEED = 0.75; // Define climb sped
  private boolean top_arm;
  private boolean bottom_arm;
  private boolean top_hatch;
  private boolean bottom_hatch;
  public double DEADZONE = 0.1;


  public DefaultClimb() {
    requires(Robot.climb);
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    Climb.target_C_A = Robot.climb.getTalon(1).getSelectedSensorPosition(0);
    Climb.target_C_H = Robot.climb.getTalon(0).getSelectedSensorPosition(0);

  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {
    top_arm = Robot.talonHelper.getClimbTopArm();
    bottom_arm = Robot.talonHelper.getClimbBottomArm();
    top_hatch = Robot.talonHelper.getClimbTopHatch();
    bottom_hatch = Robot.talonHelper.getClimbBottomHatch();
      if (OI.CLIMB_PID){
        //for the ARM side 
        if (OI.gunner_button_Y.get()) { 
          System.out.println("Y is clicked");
          Climb.target_C_A += 10;
          Climb.target_C_H += 10;
        } else if (OI.gunner_button_B.get()) {  
          System.out.println("B is clicked");
          Climb.target_C_A -= 10;
          Climb.target_C_H -= 10;
        } else {
          //do nothing
        }
        /// HATCH 0
        // ARM 1
        //set the values
        Robot.climb.getTalon(1).set(ControlMode.Position,Climb.target_C_A);
        Robot.climb.getTalon(0).set(ControlMode.Position,Climb.target_C_H);
      } else if (OI.gunner_button_B.get()) {  // Up (Front)
        System.out.println("B is clicked");
        Robot.climb.getTalon(0).set(ControlMode.PercentOutput,.5);
      } else if (OI.gunner_button_Y.get()) {  // Down (Front)
        System.out.println("Y is clicked");
        Robot.climb.getTalon(0).set(ControlMode.PercentOutput,-.75);
      } else {
        Robot.climb.getTalon(0).set(ControlMode.PercentOutput,0);

      }
         if (OI.gunner_button_A.get()) {  // Up (Back)
        System.out.println("X is clicked");
        Robot.climb.getTalon(1).set(ControlMode.PercentOutput,.5);
      } else if (OI.gunner_button_X.get()) {  // Down (Back)
        System.out.println("A is clicked");
        Robot.climb.getTalon(1).set(ControlMode.PercentOutput,-.75);
      } else {
        Robot.climb.getTalon(1).set(ControlMode.PercentOutput,0);
      }
    //}

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




        /*if (OI.gunner_button_X.get()) { 
          System.out.println("X is clicked");

        } else if (OI.gunner_button_A.get()) {  
          System.out.println("A is clicked");

        } else {
        }*/