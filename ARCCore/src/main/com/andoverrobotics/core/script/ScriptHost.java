package com.andoverrobotics.core.script;

import java.io.IOException;
import java.io.Reader;

public interface ScriptHost<ST> {
  void enterContext();

  void wrapJavaObject(Object obj, String symbol);
  Object execute(String script);
  Object execute(Reader scriptSource) throws IOException;
  Object get(String symbol);

  ST compile(String script);
  ST compile(Reader scriptSource) throws IOException;
  Object execute(ST script);

  void exitContext();
}
