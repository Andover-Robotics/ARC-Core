package com.andoverrobotics.core.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;

public class RhinoScriptHostTest {
  private ScriptHost<Script> host;

  @Before
  public void before() {
    host = new RhinoScriptHost();
    host.enterContext();
  }

  @After
  public void after() {
    host.exitContext();
  }

  @Test
  public void reflectPrimitives() {
    assertEquals(2, host.execute("2"));
    assertEquals(4.52, host.execute("2 + 2.52"));
    assertEquals(true, host.execute("!!!false"));
    assertNull(host.execute("null"));
  }

  @Test
  public void get() {
    host.execute("var a = 2.4; var b = 'hello world'; var c = false; var d = {}");
    assertEquals(2.4, host.get("a"));
    assertEquals("hello world", host.get("b"));
    assertEquals(false, host.get("c"));
    assertNull(host.get("nonexistentObject"));
  }

  @Test
  public void wrapJavaObject() {
    Runnable mock = Mockito.mock(Runnable.class);
    host.wrapJavaObject(mock, "runner");
    host.execute("runner.run()");
    verify(mock).run();
  }

  @Test
  public void executeReader() throws IOException {
    Runnable task = Mockito.mock(Runnable.class);
    host.wrapJavaObject(task, "task");
    host.execute(new StringReader("task.run()"));
    verify(task).run();
  }

  public interface Supplier<T> {
    T supply();
  }

  @Test
  public void compile() {
    Supplier task = Mockito.mock(Supplier.class);
    when(task.supply()).thenReturn("hello");
    host.wrapJavaObject(task, "task");

    Script script = host.compile("task.supply()");
    Object output = host.execute(script);

    verify(task).supply();
    assertEquals("hello", Context.toString(output));
  }
}