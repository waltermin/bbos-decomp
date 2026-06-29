package net.rim.device.internal.io;

import java.io.IOException;

public final class SocketIOException extends IOException {
   private int _exceptionCode;
   public static final int SOC_ERROR_BAD_SID = -6;

   public SocketIOException(int exceptionCode) {
      this._exceptionCode = exceptionCode;
   }

   @Override
   public final String toString() {
      return "SocketIOException(" + this._exceptionCode + ')';
   }
}
