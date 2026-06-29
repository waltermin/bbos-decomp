package net.rim.ecmascript.runtime;

public class ESReferenceError extends ESError {
   public ESReferenceError(String message) {
      super("ReferenceError", message, GlobalObject.getInstance().referenceErrorPrototype);
   }
}
