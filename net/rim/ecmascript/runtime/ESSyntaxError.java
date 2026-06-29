package net.rim.ecmascript.runtime;

public class ESSyntaxError extends ESError {
   public ESSyntaxError(String message) {
      super("SyntaxError", message, GlobalObject.getInstance().syntaxErrorPrototype);
   }
}
