package com.andoverrobotics.core.tasks;

import bsh.EvalError;
import bsh.Interpreter;
import org.junit.Test;

public class BeanTest {
  @Test
  public void beanShellRuns() throws EvalError {
    Interpreter interpreter = new Interpreter();
    interpreter.eval("print(\"hello, world!\")");
  }
}
