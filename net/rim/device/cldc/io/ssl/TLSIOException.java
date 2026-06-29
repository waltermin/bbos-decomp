package net.rim.device.cldc.io.ssl;

import javax.microedition.pki.CertificateException;

public class TLSIOException extends CertificateException {
   private TLSException _e;

   public TLSIOException(TLSException e) {
      super(e.getMessage(), null, (byte)0);
      this._e = e;
   }

   public TLSIOException(String msg) {
      super(msg, null, (byte)0);
   }

   public TLSIOException(TLSException e, String msg) {
      super(msg, null, (byte)0);
      this._e = e;
   }

   public TLSException getException() {
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
