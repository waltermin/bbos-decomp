package net.rim.ecmascript.runtime;

public class ESEvalError extends ESError {
   public ESEvalError(String message) {
      super("EvalError", message, GlobalObject.getInstance().evalErrorPrototype);
   }
}
