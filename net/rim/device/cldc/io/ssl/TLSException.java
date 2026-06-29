package net.rim.device.cldc.io.ssl;

public class TLSException extends Exception {
   private Exception _e;

   public TLSException() {
   }

   public TLSException(Exception e) {
      this._e = e;
   }

   public TLSException(String msg) {
   }

   public TLSException(Exception e, String msg) {
      super(msg);
      this._e = e;
   }

   public Exception getException() {
      return this._e;
   }

   @Override
   public String toString() {
      StringBuffer buffer = (StringBuffer)(new Object(super.toString()));
      if (this._e != null) {
         buffer.append('(');
         buffer.append(this._e.toString());
         buffer.append(')');
      }

      return buffer.toString();
   }
}
