package net.rim.device.api.crypto;

import java.io.IOException;

public class CryptoIOException extends IOException {
   private CryptoException _e;

   public CryptoIOException(CryptoException e) {
      this._e = e;
   }

   public CryptoException getCryptoException() {
      return this._e;
   }

   public CryptoIOException(String msg) {
   }

   @Override
   public String toString() {
      return this._e == null
         ? super.toString()
         : ((StringBuffer)(new Object())).append(super.toString()).append(" ( ").append(this._e.toString()).append(" )").toString();
   }
}
