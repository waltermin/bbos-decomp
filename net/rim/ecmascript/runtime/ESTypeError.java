package net.rim.ecmascript.runtime;

public class ESTypeError extends ESError {
   public ESTypeError(String message) {
      super("TypeError", message, GlobalObject.getInstance().typeErrorPrototype);
   }
}
