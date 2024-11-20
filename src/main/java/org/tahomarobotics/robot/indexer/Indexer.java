package org.tahomarobotics.robot.indexer;

import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import edu.wpi.first.wpilibj.DigitalInput;
import org.tahomarobotics.robot.RobotMap;
import org.tahomarobotics.robot.util.SubsystemIF;

public class Indexer extends SubsystemIF {
    private static final Indexer INSTANCE = new Indexer();

    // MOTORS
    private final TalonFX topMotor = new TalonFX(RobotMap.INDEXER_MOTOR);
    private final TalonFX bottomMotor = new TalonFX(RobotMap.INDEXER_MOTOR);


    // BEAM BRAKES
    // Welcome To The Amazing Digital Input
    private final DigitalInput collectorBeamBrake = new DigitalInput(RobotMap.BEAM_BREAK_ONE);
    private final DigitalInput shooterBeamBrake = new DigitalInput(RobotMap.BEAM_BREAK_TWO);

    private final MotionMagicVelocityVoltage Velocity = new MotionMagicVelocityVoltage(0);

    // CONTROL REQUESTS



    // STATUS SIGNALS


    // STATE
    //I SENTENCE YOU TO BE STATED
    private State stateDisable = State.DISABLED;
    private State stateIntake = State.INTAKING;
    private State stateIndex = State.INDEXING;
    private State stateCollect = State.COLLECTED;
    private State stateEject = State.EJECTING;

private Indexer() {
    bottomMotor.getConfigurator().apply(IndexerConstants.indexMotorConfiguration);
    topMotor.getConfigurator().apply(IndexerConstants.indexMotorConfiguration);
    topMotor.setInverted(true);
}

    public static Indexer getInstance() {
        return INSTANCE;
    }

    // GETTERS

    public double getTopMotorVelocity() {
        return topMotor.getVelocity().getValueAsDouble();
    }

    public double getBottomMotorVelocity() {
        return bottomMotor.getVelocity().getValueAsDouble();
    }

    public boolean getBeamBrakeOneBreakage() {
        return collectorBeamBrake.get();
    }

    public boolean getBeamBrakeTwoBreakage() {
        return shooterBeamBrake.get();
    }

    // SETTERS

    private void setMotorVelocity(double velocity) {
        bottomMotor.setControl(Velocity.withVelocity(velocity));
    }

    public void setMotorVoltage(double voltage) {
        bottomMotor.setVoltage(voltage);
        topMotor.setVoltage(voltage);
    }


    // STATE MACHINE




    // STATES

    public enum State {
        DISABLED,
        INTAKING,
        INDEXING,
        COLLECTED,
        EJECTING,
    }
}
