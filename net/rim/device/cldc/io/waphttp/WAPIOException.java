package net.rim.device.cldc.io.waphttp;

import java.io.IOException;

public final class WAPIOException extends IOException {
   private int _error;
   private int _additionalData;
   public static final int USER_ABORTED;
   public static final int CONNECT_SUCCESS_FETCH_UNSUCESSFUL;
   public static final int MAX_REDIRECTS_EXCEEDED;
   public static final int UNABLE_TO_CONNECT;
   public static final int WTLS_NOT_INSTALLED;
   public static final int WAP_ABORT;
   public static final int PAGE_TOO_LARGE;
   public static final int WTLS_EXCEPTION;
   public static final int WTLS_IO_EXCEPTION;
   public static final int SESSION_FAILED;

   public WAPIOException(int error) {
      this._error = error;
   }

   public WAPIOException(int error, int additionalData) {
      this._error = error;
      this._additionalData = additionalData;
   }

   public final int getError() {
      return this._error;
   }

   public final int getAdditionalData() {
      return this._additionalData;
   }
}
