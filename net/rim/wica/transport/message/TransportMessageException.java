package net.rim.wica.transport.message;

public class TransportMessageException extends Exception {
   private int _errorCode;
   private Throwable _cause;
   public static final int FORMAT_ERROR = 0;
   public static final int BUNDLING_INCOMPATIBLE_VERSIONS_ERROR = 100;
   public static final int BUNDLING_ENCRYPTED_MESSAGES_ERROR = 101;
   public static final int DEBUNDLING_ENCRYPTED_MESSAGE_ERROR = 102;
   public static final int MAX_BUNDLE_SIZE_EXCEEDED = 103;

   public TransportMessageException(int errorCode) {
      this._errorCode = errorCode;
   }

   public TransportMessageException(int errorCode, String s) {
      super(s);
      this._errorCode = errorCode;
   }

   public TransportMessageException(int errorCode, Throwable cause) {
      this._errorCode = errorCode;
      this._cause = cause;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public Throwable getCause() {
      return this._cause;
   }

   @Override
   public String getMessage() {
      return ((StringBuffer)(new Object())).append(super.getMessage()).append(", Error Code: ").append(String.valueOf(this._errorCode)).toString();
   }
}
