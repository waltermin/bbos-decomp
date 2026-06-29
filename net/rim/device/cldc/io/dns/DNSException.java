package net.rim.device.cldc.io.dns;

import java.io.IOException;

public final class DNSException extends IOException {
   private int _errorCode;

   public DNSException() {
   }

   public DNSException(String s) {
      super(s);
   }

   public DNSException(String s, int errorCode) {
      super(s);
      this._errorCode = errorCode;
   }

   public DNSException(int errcode) {
      super("DNS error");
      this._errorCode = errcode;
   }

   public final int getErrorCode() {
      return this._errorCode;
   }
}
