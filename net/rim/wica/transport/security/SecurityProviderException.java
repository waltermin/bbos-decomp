package net.rim.wica.transport.security;

public class SecurityProviderException extends Exception {
   private String _message;
   private Throwable _cause;

   public SecurityProviderException() {
   }

   public SecurityProviderException(String message) {
      this._message = message;
   }

   public SecurityProviderException(Throwable cause) {
      this._cause = cause;
   }

   public SecurityProviderException(String message, Throwable cause) {
      this._message = message;
      this._cause = cause;
   }

   public Throwable getCause() {
      return this._cause;
   }

   @Override
   public String getMessage() {
      return this._message;
   }
}
