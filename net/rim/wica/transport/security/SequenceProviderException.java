package net.rim.wica.transport.security;

public class SequenceProviderException extends Exception {
   private String _message;
   private Throwable _cause;

   public SequenceProviderException() {
   }

   public SequenceProviderException(String message) {
      this._message = message;
   }

   public SequenceProviderException(Throwable cause) {
      this._cause = cause;
   }

   public SequenceProviderException(String message, Throwable cause) {
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
