package net.rim.wica.transport.security;

public class SecureMessageException extends Exception {
   private int _errorCode;
   private Throwable _primaryCause;
   private Throwable _secondaryCause;
   public static final int FORMAT_ERROR = 0;
   public static final int KEY_RETREIVAL_FAILED = 1;
   public static final int SEQUENCE_VERIFICATION_FAILED = 100;
   public static final int SIGNATURE_VERIFICATION_FAILED = 101;
   public static final int DECRYPTION_FAILED = 102;
   public static final int UNSECURE_EXPECTED_SIGNED = 103;
   public static final int UNEXPECTED_VERSION = 104;
   public static final int SIGNATURE_FAILED = 200;
   public static final int ENCRYPTION_FAILED = 201;
   public static final int SEQUENCE_GENERATION_FAILED = 202;

   SecureMessageException(String s) {
   }

   public SecureMessageException(int errorCode) {
      this._errorCode = errorCode;
   }

   public SecureMessageException(int errorCode, String s) {
      super(s);
      this._errorCode = errorCode;
   }

   public SecureMessageException(int errorCode, Throwable cause) {
      this._errorCode = errorCode;
      this._primaryCause = cause;
   }

   public SecureMessageException(int errorCode, Throwable primaryCause, Throwable secondaryCause) {
      this._errorCode = errorCode;
      this._primaryCause = primaryCause;
      this._secondaryCause = secondaryCause;
   }

   public int getErrorCode() {
      return this._errorCode;
   }

   public boolean hasCause() {
      return this._primaryCause != null;
   }

   public Throwable getCause() {
      return this._primaryCause;
   }

   public boolean hasSecondaryCause() {
      return this._secondaryCause != null;
   }

   public Throwable getSecondaryCause() {
      return this._secondaryCause;
   }

   @Override
   public String getMessage() {
      return ((StringBuffer)(new Object())).append(super.getMessage()).append(", Error Code: ").append(String.valueOf(this._errorCode)).toString();
   }
}
