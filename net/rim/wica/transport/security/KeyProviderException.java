package net.rim.wica.transport.security;

public class KeyProviderException extends Exception {
   private Throwable _cause;

   public KeyProviderException() {
   }

   public KeyProviderException(String message) {
   }

   public KeyProviderException(Throwable cause) {
      this._cause = cause;
   }

   public KeyProviderException(String message, Throwable cause) {
      super(message);
      this._cause = cause;
   }

   public Throwable getCause() {
      return this._cause;
   }
}
