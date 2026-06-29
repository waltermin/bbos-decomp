package com.fourthpass.wmls;

import net.rim.device.api.browser.field.RenderingApplication;

public final class Interpreter {
   private WMLScript _script;
   private String _method;
   private Value[] _args;
   private Value _result;
   private IBrowser _browser;
   private IDialog _dialog;
   private StandardLibrary _library;
   private boolean _exited;
   private Interpreter$Engine _engine;
   private RenderingApplication _renderingApplication;
   private static final int MAX_CALL_STACK_SIZE = 200;
   private static final String CONTENT_TYPE_WMLSC = "application/vnd.wap.wmlscriptc";
   private static final String HEADER_CONTENT_TYPE = "Content-Type";

   public Interpreter(IBrowser browser, IDialog dialog, RenderingApplication renderingApplication) {
      this._browser = browser;
      this._dialog = dialog;
      this._renderingApplication = renderingApplication;
      this._library = new StandardLibrary();
   }

   public final Value exec(URL url, WMLScript script, String method, String[] arg) {
      this._script = script;
      this._method = method;
      this._args = new Value[arg.length];

      for (int i = 0; i < arg.length; i++) {
         this._args[i] = Value.parseLiteral(arg[i]);
      }

      synchronized (this) {
         this._engine = new Interpreter$Engine(this);
      }

      this._engine.interpret();
      synchronized (this) {
         this._engine = null;
      }

      return this._result;
   }

   public final synchronized void abort() {
      if (this._engine != null) {
         this._engine.halt();
      }
   }

   final boolean hasExited() {
      return this._exited;
   }
}
