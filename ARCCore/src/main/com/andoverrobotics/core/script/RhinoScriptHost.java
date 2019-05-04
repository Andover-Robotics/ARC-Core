package com.andoverrobotics.core.script;

import java.io.IOException;
import java.io.Reader;
import org.mozilla.javascript.*;

public class RhinoScriptHost implements ScriptHost<Script> {
  private Context ctx;
  private Scriptable scope;

  @Override
  public void enterContext() {
    ctx = Context.enter();
    scope = ctx.initStandardObjects();
  }

  @Override
  public void wrapJavaObject(Object obj, String symbol) {
    Object wrapped = Context.javaToJS(obj, scope);
    ScriptableObject.putProperty(scope, symbol, wrapped);
  }

  @Override
  public Object execute(String script) {
    return ctx.evaluateString(scope, script, "<cmd>", 1, null);
  }

  @Override
  public Object execute(Reader scriptSource) throws IOException {
    return ctx.evaluateReader(scope, scriptSource, scriptSource.toString(), 1, null);
  }

  @Override
  public Object get(String symbol) {
    Object result = scope.get(symbol, scope);
    return result == Scriptable.NOT_FOUND ? null : result;
  }

  @Override
  public Script compile(String script) {
    return ctx.compileString(script, "<cmd>", 1, null);
  }

  @Override
  public Script compile(Reader scriptSource) throws IOException {
    return ctx.compileReader(scriptSource, "<cmd>", 1, null);
  }

  @Override
  public Object execute(Script script) {
    return script.exec(ctx, scope);
  }

  @Override
  public void exitContext() {
    Context.exit();
  }
}
