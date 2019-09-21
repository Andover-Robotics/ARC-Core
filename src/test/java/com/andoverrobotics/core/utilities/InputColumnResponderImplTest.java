package com.andoverrobotics.core.utilities;

import com.qualcomm.robotcore.hardware.Gamepad;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InputColumnResponderImplTest {
  private boolean inputValue = false;
  private InputColumnResponder icr;

  @Before
  public void setUp() {
     icr = new InputColumnResponderImpl();
     inputValue = false;
  }

  @Test
  public void respondsToChange() {
    Runnable execute = mock(Runnable.class);

    icr.register(() -> inputValue, execute);

    inputValue = false;
    icr.update();
    inputValue = true;
    icr.update();

    verify(execute).run();
    verifyNoMoreInteractions(execute);
  }

  @Test
  public void respondsToMultipleChanges() {
    Gamepad gamepad = new Gamepad();
    Runnable a = mock(Runnable.class),
        b = mock(Runnable.class);

    icr.register(() -> gamepad.a, a)
        .register(() -> gamepad.b, b);

    // Iteration 1
    gamepad.a = false;
    gamepad.b = false;
    icr.update();
    verifyZeroInteractions(a, b);

    // Iteration 2
    gamepad.a = true;
    gamepad.b = false;
    icr.update();
    verify(a).run();
    verifyZeroInteractions(b);

    // Iteration 3
    gamepad.a = false;
    gamepad.b = true;
    icr.update();
    verifyNoMoreInteractions(a);
    verify(b).run();
  }

  @Test
  public void respondsToChangesRepeatedly() {
    Gamepad gamepad = new Gamepad();
    Runnable a = mock(Runnable.class), b = mock(Runnable.class);

    icr.register(() -> gamepad.a, a)
        .register(() -> gamepad.b, b);

    // Iteration 1
    gamepad.a = false;
    gamepad.b = false;
    icr.update();
    verifyZeroInteractions(a, b);

    // Iteration 2
    gamepad.a = true;
    gamepad.b = true;
    icr.update();
    verify(a).run();
    verify(b).run();
    reset(a, b);

    // Iteration 3
    gamepad.a = false;
    gamepad.b = true;
    icr.update();
    verifyZeroInteractions(a, b);

    // Iteration 4
    gamepad.a = true;
    gamepad.b = false;
    icr.update();
    verify(a).run();
    verifyZeroInteractions(b);
  }

  @Test
  public void clearRegistryStopsResponse() {
    Runnable run = mock(Runnable.class);
    icr.register(() -> inputValue, run);

    inputValue = true;
    icr.update();
    verify(run).run();

    inputValue = false;
    icr.update();
    verifyNoMoreInteractions(run);

    icr.clearRegistry();

    inputValue = true;
    icr.update();
    verifyNoMoreInteractions(run);
  }

  @Test
  public void clearRegistryClearsResidualState() {
    Runnable run = mock(Runnable.class);
    icr.register(() -> inputValue, run);

    inputValue = true;
    icr.update();
    verify(run).run();
    reset(run);

    icr.clearRegistry();

    inputValue = false;
    icr.update();

    icr.register(() -> inputValue, run);
    inputValue = true;
    icr.update();
    // If the state before clearRegistry was not cleared, then it would consider the input value as unchanged
    // (from true to true), so it will not run the callback. We don't want this.
    verify(run).run();
  }
}