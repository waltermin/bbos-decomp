package net.rim.wica.runtime.messaging;

public class MessageException extends Exception {
   private Throwable _cause;

   public MessageException() {
   }

   public MessageException(String arg0) {
   }

   public MessageException(Throwable cause) {
      this._cause = cause;
   }

   public MessageException(String arg0, Throwable cause) {
      super(arg0);
      this._cause = cause;
   }

   public Throwable getCause() {
      return this._cause;
   }

   @Override
   public String toString() {
      StringBuffer s = (StringBuffer)(new Object(super.toString()));
      if (this._cause != null) {
         s.append(" Cause: ").append(this._cause.toString());
      }

      return s.toString();
   }
}
