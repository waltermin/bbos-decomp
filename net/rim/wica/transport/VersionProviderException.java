package net.rim.wica.transport;

public class VersionProviderException extends Exception {
   private Throwable _cause;

   public VersionProviderException() {
   }

   public VersionProviderException(String message) {
   }

   public VersionProviderException(Throwable cause) {
      this._cause = cause;
   }

   public VersionProviderException(String message, Throwable cause) {
      super(message);
      this._cause = cause;
   }

   public Throwable getCause() {
      return this._cause;
   }
}
