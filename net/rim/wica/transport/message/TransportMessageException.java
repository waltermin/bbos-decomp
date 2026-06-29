package net.rim.wica.transport.message;

public class TransportMessageException extends Exception {
   private int _errorCode;
   private Throwable _cause;
   public static final int FORMAT_ERROR;
   public static final int BUNDLING_INCOMPATIBLE_VERSIONS_ERROR;
   public static final int BUNDLING_ENCRYPTED_MESSAGES_ERROR;
   public static final int DEBUNDLING_ENCRYPTED_MESSAGE_ERROR;
   public static final int MAX_BUNDLE_SIZE_EXCEEDED;

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
