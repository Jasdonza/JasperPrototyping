package org.tahomarobotics.robot.collector;

import com.ctre.phoenix6.controls.MotionMagicVelocityVoltage;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import org.tahomarobotics.robot.RobotMap;
import org.tahomarobotics.robot.util.SubsystemIF;

public class Collector extends SubsystemIF {

    private boolean shouldEject;
    private boolean shouldDeploy;
    private boolean shouldStow;

    private static final Collector INSTANCE = new Collector();

    // MOTORS
    private final TalonFX deployLeft = new TalonFX(RobotMap.DEPLOY_MOTOR_LEFT);
    private final TalonFX deployRight = new TalonFX(RobotMap.DEPLOY_MOTOR_RIGHT);
    private final TalonFX collectMotor = new TalonFX(RobotMap.COLLECTOR_MOTOR);
//Movement magic stuff
    private final MotionMagicVoltage collectorControl = new MotionMagicVoltage(CollectorConstants.STOW_POSITION);
    private final MotionMagicVelocityVoltage deploymentControl = new MotionMagicVelocityVoltage(0);

    // CONTROL REQUESTS

    // STATUS SIGNALS

    private DeploymentState deploymentState = DeploymentState.ZEROING,
            preEjectState;
    private CollectionState collectionState = CollectionState.DISABLED;


    @Override
    public void periodic() {
        switch (deploymentState){
            case DEPLOYED -> {
                if (!shouldDeploy) ();
                if (shouldEject) pivotEject();
            }
        }

    }


    private Collector() {
        deployLeft.getConfigurator().apply(CollectorConstants.deployMotorConfiguration);
        deployRight.getConfigurator().apply(CollectorConstants.deployMotorConfiguration);
        deployRight.setInverted(true);
        collectMotor.getConfigurator().apply(CollectorConstants.collectMotorConfiguration);
    }

    @Override
    public void onTeleopInit() {
        new CollectorZeroCommand().schedule();
    }

    public static Collector getInstance() {
        return INSTANCE;
    }


    // GETTERS

    public double getLeftPivotVelocity() {
        return deployLeft.getVelocity().getValueAsDouble();
    }

    public double getRightPivotVelocity() {
        return deployRight.getVelocity().getValueAsDouble();
    }

    // SETTERS

    private void setDeployPos(double pos) {
        deployLeft.setControl(collectorControl.withPosition(pos));
        deployRight.setControl(collectorControl.withPosition(pos));
    }

    private void setDeployVelocity(double velocity) {
    collectMotor.setControl(deploymentControl.withVelocity(velocity));
    }

    public void setDeployVoltage(double voltage) {
        deployLeft.setVoltage(voltage);
        deployRight.setVoltage(voltage);
    }

    public void zeroCollector() {
        deployLeft.setPosition(0);
        deployRight.setPosition(0);
    }



    // STATE MACHINE

    public void rollerCollect() {
        setDeployVelocity(CollectorConstants.COLLECTOR_COLLECT_VELOCITY);

        collectionState = CollectionState.COLLECTING;
    }



    // STATES

    public enum CollectionState {
        COLLECTING,
        DISABLED,
        EJECTING
    }

    public enum DeploymentState {
        DEPLOYED,
        STOWED,
        EJECT,
        ZEROING
    }
}
