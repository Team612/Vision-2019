/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.Climb;

public class AutoClimb extends Command {
  private double LIFT_SPEED_H = -.5;
  private double LIFT_SPEED_A = -.5;


  // Endgame time variables
  private final double END_GAME = 30;
  private final double MATCH_LENGTH = 150;

  // The specific values for the encoders (Where we are trying to go)
  private double TOP_ENCODER_POSITION = 400;
  private double BOTTOM_ENCODER_POSITION = 0;

  // Climb state booleans
  private boolean IN_PROGRESS = true;  // Boolean to determine when performing a function
  private boolean END = false;  // Boolean to end the command
  public static boolean IS_TILTED = false;

  private int PHASE = 1;  // Specify what phase of the case functions we are in

  private int LIFT_SPEED = 15;

  private String PHASE_STRING = "Waiting for next step";  // The string of what phase we are currently

  public AutoClimb() {
    requires(Robot.climb);  // Require the climb object
  }

  // Called just before this Command runs the first time
  @Override
  protected void initialize() {
    END = false;
    Climb.target_hatch = Robot.arm.getTalon().getSelectedSensorPosition(1);
    Climb.target_arm = Robot.arm.getTalon().getSelectedSensorPosition(0);
  }

  private void lift_frame() {  // Set the climb talons to an elevated position at level to the HAB platform
    Robot.climb.getTalon(0).set(ControlMode.PercentOutput, LIFT_SPEED_H);
    Robot.climb.getTalon(1).set(ControlMode.PercentOutput, LIFT_SPEED_A);
    if(Robot.climb.getTalon(0).getSelectedSensorPosition(0) >  Robot.climb.getTalon(1).getSelectedSensorPosition(0) + 100){
      LIFT_SPEED_A = LIFT_SPEED_A * .95;
      LIFT_SPEED_H = LIFT_SPEED_H * 1.05;
    }
    else if(Robot.climb.getTalon(1).getSelectedSensorPosition(0) >  Robot.climb.getTalon(0).getSelectedSensorPosition(0) + 100){
      LIFT_SPEED_H = LIFT_SPEED_H * .95;
      LIFT_SPEED_A = LIFT_SPEED_A * 1.05;
    }
    //Climb.target_hatch += LIFT_SPEED;
    //Climb.target_arm += LIFT_SPEED;

    // Set the lift talons to target apex position
    //Robot.climb.getTalon(1).set(ControlMode.Position, Climb.target_arm);
    //Robot.climb.getTalon(0).set(ControlMode.Position, Climb.target_hatch);

    // Ternary statements to determine lift talon positions
    boolean hatch_lifted = Robot.limit_switch.getClimbTopHatch();
    boolean arm_lifted = Robot.limit_switch.getClimbTopArm();

    if (hatch_lifted && arm_lifted) {  // Check if the front and back are at the optimal positions
      IN_PROGRESS = false;  // Finish the lift frame function
    }

  }

  private void lift_hatch_wheels() {  // Returns to wheels back from the elevated position back to the original position

    Climb.target_hatch -= LIFT_SPEED;

    Robot.climb.getTalon(0).set(ControlMode.Position, Climb.target_hatch);  // Set the talon position back to the bottom

    // Ternary statement to determine if the position of talon is back to normal position
    boolean hatch_wheel_lifted = Robot.limit_switch.getClimbBottomHatch(); 
    
    if (hatch_wheel_lifted) {  // Check if the boolean is true
      IN_PROGRESS = false;  // Finish the lift front wheels function
    }

  }

  private void lift_arm_wheels() {  // Returns the back wheels back from the elevated position to the original position
    
    Climb.target_arm -= LIFT_SPEED;

    Robot.climb.getTalon(1).set(ControlMode.Position, BOTTOM_ENCODER_POSITION);  // Set the talon position back to the bottom
    
    // Ternary statement to determine if the position of talon is back to normal position
    boolean arm_wheel_lifted = Robot.limit_switch.getClimbBottomArm(); 
    
    if (arm_wheel_lifted) {  // Check if the boolean is true
      IN_PROGRESS = false;  // Finish the lift back wheels function
    }

  }

  // Called repeatedly when this Command is scheduled to run
  @Override
  protected void execute() {

    if (!OI.KILL_CLIMB) {  // Only run if climb is enabled

      //if (Timer.getMatchTime() < (MATCH_LENGTH - END_GAME)) {  // Only run this execute loop if the match time is in the endgame
        
        /*if(Robot.climb.getNavX().getPitch() >= 20){  // If pitch is >20 degrees

          IS_TILTED = true;
          OI.driver.setRumble(GenericHID.RumbleType.kLeftRumble, 1); 
          OI.driver.setRumble(GenericHID.RumbleType.kRightRumble, 1); 

          if (OI.driver_button_BCK.get()) {  // Allow drive override
            IN_PROGRESS = false;
            OI.LOCK_DRIVETRAIN = false;
            PHASE = 0;
            OI.driver.setRumble(GenericHID.RumbleType.kLeftRumble, 0); 
            OI.driver.setRumble(GenericHID.RumbleType.kRightRumble, 0); 
          }

        } else {

          IS_TILTED = false;
          OI.driver.setRumble(GenericHID.RumbleType.kLeftRumble, 0); 
          OI.driver.setRumble(GenericHID.RumbleType.kRightRumble, 0); 
        }*/

          if (IN_PROGRESS) {  // Only run this case statement if a phase function is called
            
            switch( PHASE ) {  // Switch case for the phase stepS
                case 1:
                PHASE_STRING = "Lifting the Chassis";
                lift_frame();  // At step 1, secondly, lift the entire robot frame up except for the wheels
              case 2:
                PHASE_STRING = "Lifting the Front Wheels";
                lift_hatch_wheels();  // At step 2, thirdly, lift the front wheels of the robot to drive onto the HAB
              case 3:
                PHASE_STRING = "Lifting the Back Wheels";
                lift_arm_wheels();  // At step 3, fourth, lift the back wheels to elevate the entire robot
              default:
                PHASE_STRING = "Climb is Currently not Running";
                System.out.println("Climb has ended");
                END = true;  // Exit the command

            }

        }

        if (!IN_PROGRESS && OI.driver_button_A.get()) {  // If we are not in progress and the driver clicks the A button
          
          OI.LOCK_DRIVETRAIN = true;  // Lock the drivetrain
          IN_PROGRESS = true;  // Set in progress to true
          PHASE++;  // Go to the next step
        
        } else {
          OI.LOCK_DRIVETRAIN = false;
        }

        SmartDashboard.putString("Climb Phase", PHASE_STRING);  // Put the current phase string to SmartDashboard

      } else {
        END = true;
      }

   /* } else {
      END = true;
    } */

  }

  // Make this return true when this Command no longer needs to run execute()
  @Override
  protected boolean isFinished() {
    return END;  // End the command based on the boolean
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
