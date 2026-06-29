package net.rim.device.cldc.io.ssl;

public class TLSCancelException extends TLSException {
   private Exception _e;

   public TLSCancelException(Exception e) {
      super(e);
      this._e = e;
   }

   public TLSCancelException(String msg) {
      super(msg);
      this._e = null;
   }

   public TLSCancelException(Exception e, String msg) {
      super(e, msg);
      this._e = e;
   }

   @Override
   public Exception getException() {
      return this._e;
   }
}
