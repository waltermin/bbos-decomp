package net.rim.ecmascript.runtime;

public class ESURIError extends ESError {
   public ESURIError(String message) {
      super("URIError", message, GlobalObject.getInstance().URIErrorPrototype);
   }
}
