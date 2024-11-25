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

    private boolean shouldCollect;

    private static final Collector INSTANCE = new Collector();

    // MOTORS
    //THEY ORS A MOT
    private final TalonFX deployLeft = new TalonFX(RobotMap.DEPLOY_MOTOR_LEFT);
    private final TalonFX deployRight = new TalonFX(RobotMap.DEPLOY_MOTOR_RIGHT);
    private final TalonFX collectMotor = new TalonFX(RobotMap.COLLECTOR_MOTOR);

    //Movement magic stuff
    private final MotionMagicVoltage collectorControl = new MotionMagicVoltage(CollectorConstants.STOW_POSITION);
    private final MotionMagicVelocityVoltage deploymentControl = new MotionMagicVelocityVoltage(0);

    // CONTROL REQUESTS
    //I REQUEST CONTROL NOW

    public void ToggleDeploy () {

    }

    public void setShouldDeploy(boolean shouldbeDeploy) {
        this.shouldDeploy = shouldbeDeploy;
    }
        public void setShouldCollect(boolean shouldCollect) {
            this.shouldCollect = shouldCollect;
    }



    @Override
    public void periodic() {
        switch (deploymentState) {
            case DEPLOYED -> {
                if (shouldDeploy) deploymentDeploy();
                if (!shouldDeploy) deploymentStow();
                if (shouldEject) collectorEject();
            }
            case EJECT -> {
                if (!shouldEject) deploymentUnEject();
            }
            case STOWED -> {
                if (!shouldStow) deploymentDeploy();
                if (shouldEject) deploymentEject();
            }
            case ZEROING -> {
            }
        }

        switch (collectionState) {
            case COLLECTING -> {
                if (shouldCollect) collectorCollect();
                if (!shouldDeploy) collectorDisabled();
                if (shouldEject) collectorEject();
            }
            case EJECTING -> {
            }
            case DISABLED -> {
                if (shouldDeploy) collectorCollect();
                if (shouldEject) collectorEject();
            }
        }
    }


    // STATUS SIGNALS
    //(TRAIN SIGNAL NOISE) FULL STEAM AHE- WAIT, THEIR ZEROING.

    private DeploymentState deploymentState = DeploymentState.ZEROING,
            preEjectState;
    private CollectionState collectionState = CollectionState.DISABLED;


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
    //"YOU HAVE THIS INFORMATION? IT'S OUR'S NOW, COMRADE"

    public double getLeftPivotVelocity() {
        return deployLeft.getVelocity().getValueAsDouble();
    }

    public double getRightPivotVelocity() {
        return deployRight.getVelocity().getValueAsDouble();
    }

    // SETTERS
    //OH BOY, HERE I GO SETTING AGAIN

    private void setDeployPos(double pos) {
        deployLeft.setControl(collectorControl.withPosition(pos));
        deployRight.setControl(collectorControl.withPosition(pos));
    }

    private void setCollectorVelocity(double velocity) {
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
    //I STATE, THE MACHINE

    public void collectorCollect() {
        setCollectorVelocity(CollectorConstants.COLLECTOR_COLLECT_RPS);

        collectionState = CollectionState.COLLECTING;
    }

    public void collectorDisabled () {
        collectMotor.stopMotor();

        collectionState = CollectionState.DISABLED;
    }

    public void collectorEject () {
        setCollectorVelocity(CollectorConstants.COLLECTOR_EJECT_VELOCITY);

        collectionState = CollectionState.EJECTING;
    }

    public void deploymentDeploy() {
        setDeployPos(CollectorConstants.DEPLOYMENT_DEPLOY_POSITION);

        deploymentState = DeploymentState.DEPLOYED;
    }

    public void  deploymentEject() {
        setDeployPos(CollectorConstants.EJECT_POSITION);

        deploymentState = DeploymentState.EJECT;
    }

    public void deploymentUnEject() {
        switch (preEjectState) {
            case DEPLOYED ->  deploymentDeploy();
            case STOWED ->  deploymentStow();
            default -> {}
        }
    }

    public void deploymentStow() {
        setDeployPos(CollectorConstants.STOW_POSITION);

        preEjectState = deploymentState;
        deploymentState = DeploymentState.STOWED;
    }

    public CollectionState getCollectionState() {
        return collectionState;
    }

    // THE (UNITED) STATES

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
