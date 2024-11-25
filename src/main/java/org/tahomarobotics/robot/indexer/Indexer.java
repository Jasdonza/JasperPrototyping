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

    // STATE
    //I SENTENCE YOU TO BE STATED
    private boolean shouldEject;
    private boolean shouldIntake;
    private boolean shouldIndex;
    private boolean shouldDisable;
    private boolean shouldHaveCollect;

    private State state = State.DISABLED;

    // CONTROL REQUESTS
    //SIR! I REQUEST CONTROL OF THE INDEXER!

    public void setShouldEject(boolean shouldEject) {
        this.shouldEject = shouldEject;
    }

    @Override
    public void periodic() {
        switch (state) {
            case INTAKING -> {
                if (shouldIntake) stateIntake();
                if (!shouldIndex) stateIntake();
            }
            case INDEXING -> {
                if (getBeamBrakeOneBreakage()) stateIndex();
            }
            case DISABLED -> {
                if (shouldDisable) stateDisable();
            }
            case EJECTING -> {
                if (shouldEject) stateEject();
            }
            case COLLECTED -> {
                if (getBeamBrakeTwoBreakage()) stateCollected();
            }
        }
    }

    // STATUS SIGNALS



    private Indexer() {
                bottomMotor.getConfigurator().apply(IndexerConstants.indexMotorConfiguration);
                topMotor.getConfigurator().apply(IndexerConstants.indexMotorConfiguration);
                topMotor.setInverted(true);
            }

            public static Indexer getInstance () {
                return INSTANCE;
            }

            // GETTERS

            public double getTopMotorVelocity () {
                return topMotor.getVelocity().getValueAsDouble();
            }

            public double getBottomMotorVelocity () {
                return bottomMotor.getVelocity().getValueAsDouble();
            }

            public boolean getBeamBrakeOneBreakage () {
                return collectorBeamBrake.get();
            }

            public boolean getBeamBrakeTwoBreakage () {
                return shooterBeamBrake.get();
            }

            // SETTERS

            private void setMotorVelocity ( double velocity){
                bottomMotor.setControl(Velocity.withVelocity(velocity));
            }

            public void setMotorVoltage ( double voltage){
                bottomMotor.setVoltage(voltage);
                topMotor.setVoltage(voltage);
            }

            // STATE MACHINE

            public void stateIntake () {
                setMotorVelocity(IndexerConstants.INTAKE_RPS);

                State stateIntake = State.INTAKING;
            }

            public void stateIndex () {
                setMotorVelocity(IndexerConstants.INDEX_RPS);

                State stateIndex = State.INDEXING;
            }

            public void stateCollected () {
                setMotorVelocity(IndexerConstants.STOP_RPS);

                State stateCollect = State.COLLECTED;
            }

            public void stateEject () {
                setMotorVelocity(IndexerConstants.EJECT_RPS);

                State stateEject = State.EJECTING;
            }

            public void stateDisable () {
                setMotorVelocity(IndexerConstants.STOP_RPS);

                state = State.DISABLED;
            }


            // STATES

            public enum State {
                DISABLED,
                INTAKING,
                INDEXING,
                COLLECTED,
                EJECTING,
            }
        }
