package org.tahomarobotics.robot.collector;

import edu.wpi.first.wpilibj2.command.Command;

public class CollectorZeroCommand extends Command {
    public Collector collector = Collector.getInstance();

    public CollectorZeroCommand() {
        addRequirements(collector);
    }

    @Override
    public void execute() {
        collector.setDeployVoltage(-2);
    }

    @Override
    public void end(boolean interrupted) {
        collector.setDeployVoltage(0);
        collector.zeroCollector();
    }

    @Override
    public boolean isFinished() {
        if (collector.getLeftPivotVelocity() > -.5 ) {
            return true;
        } return false;
    }
}
