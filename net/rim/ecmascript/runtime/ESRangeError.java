package net.rim.ecmascript.runtime;

public class ESRangeError extends ESError {
   public ESRangeError(String message) {
      super("RangeError", message, GlobalObject.getInstance().rangeErrorPrototype);
   }
}
