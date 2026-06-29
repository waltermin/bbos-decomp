package net.rim.device.cldc.io.ippp;

import java.io.IOException;

public final class SocketBaseIOException extends IOException {
   private int exceptionCode;
   public static final int GENERAL_EXCEPTION = 127;
   public static final int EXTERNAL_TIMEOUT_EXCEPTION = 130;

   public SocketBaseIOException() {
   }

   public SocketBaseIOException(String message) {
   }

   public final void setExceptionCode(int newCode) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   public final int getExceptionCode() {
      return this.exceptionCode;
   }
}
