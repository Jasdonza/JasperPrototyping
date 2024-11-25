// Copyright (c) FIRST and other WPILib contributors.

// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.tahomarobotics.robot;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import org.tahomarobotics.robot.collector.Collector;
import org.tahomarobotics.robot.indexer.Indexer;
import org.tahomarobotics.robot.util.SubsystemIF;

import static java.lang.Boolean.TRUE;

public class OI extends SubsystemIF {
    private final static OI INSTANCE = new OI();

    public static OI getInstance() {
        return INSTANCE;
    }

    private final CommandXboxController driveController = new CommandXboxController(0);
    private final CommandXboxController manipController = new CommandXboxController(1);

    private OI() {
        // Disable OI periodic unless its being used.
        CommandScheduler.getInstance().unregisterSubsystem(this);

        configureBindings();
    }
    /**
     * Configure the button bindings for the controller(s).
     */

    private void configureBindings() {
        Collector collector = Collector.getInstance();
        Indexer indexer = Indexer.getInstance();



        driveController.leftBumper().onTrue(Commands.runOnce(() -> collector.setShouldDeploy(true)))
                .onFalse(Commands.runOnce(() -> collector.setShouldDeploy(false)));

        driveController.leftTrigger().whileTrue(Commands.run(() -> collector.setShouldCollect(true)))
                .whileFalse(Commands.runOnce(() -> collector.setShouldCollect(false)));

        driveController.povLeft().onTrue((Commands.runOnce(() -> indexer.setShouldEject(true))))
                .onFalse((Commands.runOnce(() -> indexer.setShouldEject(false))));
    }
}
//I still don't understand OI.