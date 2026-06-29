package net.rim.device.cldc.io.waphttp;

import java.io.IOException;

public final class WAPIOException extends IOException {
   private int _error;
   private int _additionalData;
   public static final int USER_ABORTED = 1000;
   public static final int CONNECT_SUCCESS_FETCH_UNSUCESSFUL = 1001;
   public static final int MAX_REDIRECTS_EXCEEDED = 1002;
   public static final int UNABLE_TO_CONNECT = 1003;
   public static final int WTLS_NOT_INSTALLED = 1004;
   public static final int WAP_ABORT = 1005;
   public static final int PAGE_TOO_LARGE = 1006;
   public static final int WTLS_EXCEPTION = 1007;
   public static final int WTLS_IO_EXCEPTION = 1008;
   public static final int SESSION_FAILED = 1009;

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
