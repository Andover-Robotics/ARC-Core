package org.firstinspires.ftc.teamcode.teleop;

import com.andoverrobotics.core.utilities.Coordinate;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.Bot;

import java.util.function.Consumer;
import java.util.function.Predicate;

public enum ControlMode implements IControlMode {
  DRIVE(g -> g.x, gamepad -> {

    final Coordinate leftTarget = Coordinate.fromXY(gamepad.left_stick_x, -gamepad.left_stick_y);
    final Bot bot = Bot.getInstance();

    if (Math.abs(gamepad.right_stick_x) > 0.1) {
      bot.drivetrain.setRotationPower(gamepad.right_stick_x);
    } else {
      bot.drivetrain.setStrafe(leftTarget, leftTarget.getPolarDistance());
    }
  }),
  SLIDE_1(g -> g.a, gamepad -> {

  }),
  SLIDE_2(g -> g.b, gamepad -> {

  }),
  BOTH_SLIDES(g -> g.y, gamepad -> {

  });

  /**
   * Function that checks the state of the {@link Gamepad} to determine whether its control mode
   * should be switched to this mode.
   */
  private Predicate<Gamepad> activator;

  /**
   * Function that triggers output according to the input values of the supplied {@link Gamepad}.
   */
  private Consumer<Gamepad> applier;

  ControlMode(Predicate<Gamepad> activator,
      Consumer<Gamepad> applier) {
    this.activator = activator;
    this.applier = applier;
  }

  public void apply(Gamepad gamepad) {
    applier.accept(gamepad);
  }

  public boolean shouldBeActivated(Gamepad gamepad) {
    return activator.test(gamepad);
  }
}