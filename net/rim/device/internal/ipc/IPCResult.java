package net.rim.device.internal.ipc;

public class IPCResult {
   private boolean _wasSuccessful;
   private Object _result;
   private String _message;
   public static final IPCResult FAILED_RESULT = new IPCResult(null, false);

   IPCResult(Object result, boolean wasSuccessful) {
      this(result, wasSuccessful, "");
   }

   IPCResult(Object result, boolean wasSuccessful, String message) {
      this._result = result;
      this._wasSuccessful = wasSuccessful;
      this._message = message;
   }

   public Object getResult() {
      return this._result;
   }

   public boolean wasSuccessful() {
      return this._wasSuccessful;
   }

   public String getMessage() {
      return this._message;
   }
}
