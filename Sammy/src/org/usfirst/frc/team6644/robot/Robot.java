package org.usfirst.frc.team6644.robot;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.CameraServer;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team6644.robot.subsystems.ExampleSubsystem;

import org.usfirst.frc.team6644.robot.subsystems.*;
import org.usfirst.frc.team6644.robot.commandGroups.*;
import org.usfirst.frc.team6644.robot.commands.*;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	public static DriveMotors drivemotors;
	//test
	public static Joystick joystick = new Joystick(RobotPorts.JOYSTICK.get());
	//end test

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit(){
		oi = new OI();
		drivemotors=new DriveMotors();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		//test stuff
		System.out.println("X axis channel: "+joystick.getAxisChannel(Joystick.AxisType.kX));
		System.out.println("Y axis channel: "+joystick.getAxisChannel(Joystick.AxisType.kY));
		System.out.println("Z axis channel: "+joystick.getAxisChannel(Joystick.AxisType.kZ));
		System.out.println("Twist axis channel: "+joystick.getAxisChannel(Joystick.AxisType.kTwist));
		System.out.println("Throttle axis channel: "+joystick.getAxisChannel(Joystick.AxisType.kThrottle));
		//end test stuff
		//start camera stuff
		//CameraServer.getInstance().startAutomaticCapture();//simple camera stuff
		new Thread(() -> {
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			camera.setResolution(640, 480);

			CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);

			Mat source = new Mat();
			Mat output = new Mat();

			while (!Thread.interrupted()) {
				cvSink.grabFrame(source);
				Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
				outputStream.putFrame(output);
			}
		}).start();
	//advanced camera stuff
	 //end camera stuff
	}

	/**
	 * This function is called once each time the robot enters Disabled mode. You
	 * can use it to reset any subsystem information you want to clear when the
	 * robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString code to get the
	 * auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons to
	 * the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		//might use the commented out stuff below later
		/*autonomousCommand = chooser.getSelected();

		 // String autoSelected = SmartDashboard.getString("Auto Selector", "Default");
		 // switch(autoSelected) { case "My Auto": autonomousCommand = new
		 // MyAutoCommand(); break; case "Default Auto": default: autonomousCommand = new
		 // ExampleCommand(); break; }
		 

		// schedule the autonomous command (example)
		if (autonomousCommand != null) {
			autonomousCommand.start();
		}*/
		
		//clear everything that may be on the scheduler
		Scheduler.getInstance().removeAll();
		
		//add commands to scheduler for autonomous mode
		AutonomousCommandsA autonomousCommands=new AutonomousCommandsA();
		Scheduler.getInstance().add(autonomousCommands);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}
		//clear everything that may be on the scheduler
		Scheduler.getInstance().removeAll();
		
		// add command to drive robot with joystick and send stuff to SmartDashboard
		//DriveWithJoystick drive = new DriveWithJoystick();
		DriveWithJoystickWithSensitivity drive = new DriveWithJoystickWithSensitivity();
		UpdateSmartDashboard outputs = new UpdateSmartDashboard();
		Scheduler.getInstance().add(drive);
		Scheduler.getInstance().add(outputs);
	}
	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		LiveWindow.run();
		//test stuff
		try {
			System.out.println(joystick.getRawAxis(3));
			Thread.sleep(50);
		} catch(InterruptedException e) {
			System.out.println(e);
		}
		//end test stuff
	}
}
