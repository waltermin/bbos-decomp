package net.rim.device.cldc.io.ssl;

public class TLSSecurityException extends TLSIOException {
   public TLSSecurityException(TLSException e) {
      super(e);
   }

   public TLSSecurityException(String msg) {
      super(msg);
   }

   public TLSSecurityException(TLSException e, String msg) {
      super(e, msg);
   }
}
