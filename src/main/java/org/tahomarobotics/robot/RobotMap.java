package org.tahomarobotics.robot;

import edu.wpi.first.math.geometry.Translation2d;


public class RobotMap {
    // Beam Breaks
    public final static int BEAM_BREAK_ONE = 1;
    public final static int BEAM_BREAK_TWO = 2;

    // Indexer
    public static final int INDEXER_MOTOR = 34;

    // Collector
    public final static int DEPLOY_MOTOR_LEFT = 35;
    public final static int DEPLOY_MOTOR_RIGHT = 36;
    public final static int COLLECTOR_MOTOR = 37;

    // Shooter
    public static final int SHOOTER_PIVOT_MOTOR = 33;

    // Chassis
    public static final int PIGEON = 0;


    public record SwerveModuleDescriptor(String moduleName, Translation2d offset, int driveId, int steerId,
                                         int encoderId) {
    }
}
