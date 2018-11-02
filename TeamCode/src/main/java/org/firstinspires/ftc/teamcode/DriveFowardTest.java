package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

@Autonomous(name = "Simple Autonomous", group = "ARC")
public class DriveFowardTest extends LinearOpMode {
    private DcMotorController MC_Alpha;    // Motor Controller in port 0 of Core, alpha
    private DcMotorController MC_Beta;    // Motor Controller in port 1 of Core, also beta
    private DcMotorController MC_Gamma;    //Gamma, has the like not driving motors, port 2 of core
    private ServoController SC_Theta;    //theta, port 3, does servo thigns for dustpan

    private DcMotor controller1_motorR;                         // port 1 in Motor Controller 1
    private DcMotor controller1_motorL;                         // port 2 in Motor Controller 1
    private DcMotor controller2_motorR;                         // port 1 in Motor Controller 2
    private DcMotor controller2_motorL;                         // port 2 in Motor Controller 2
    private DcMotor controller3_motorL;
    private DcMotor controller3_motorR;
    private Servo servoR;
    private Servo servoL;

    @Override
    public void runOpMode(){
        MC_Alpha = hardwareMap.dcMotorController.get("MC_Alpha");
        MC_Beta = hardwareMap.dcMotorController.get("MC_Beta");
        MC_Gamma = hardwareMap.dcMotorController.get("MC_Gamma");
        SC_Theta = hardwareMap.servoController.get("SC_Theta");


        controller1_motorR = hardwareMap.dcMotor.get("FR_Motor");
        controller1_motorL = hardwareMap.dcMotor.get("FL_Motor");
        controller2_motorR = hardwareMap.dcMotor.get("BR_Motor");
        controller2_motorL = hardwareMap.dcMotor.get("BL_Motor");
        controller3_motorL = hardwareMap.dcMotor.get("LS_Motor");
        controller3_motorR = hardwareMap.dcMotor.get("Sweeper_Motor");
        servoL = hardwareMap.servo.get("L_Dustpan");
        servoR = hardwareMap.servo.get("R_Dustpan");

        controller1_motorL.setPower(1);
        controller1_motorR.setPower(1);
        controller2_motorL.setPower(1);
        controller2_motorR.setPower(1);
    }

}
