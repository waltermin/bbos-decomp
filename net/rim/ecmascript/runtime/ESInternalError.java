package net.rim.ecmascript.runtime;

public class ESInternalError extends ESError {
   public ESInternalError(String message) {
      super("InternalError", message);
   }
}
