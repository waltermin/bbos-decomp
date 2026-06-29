package net.rim.wica.transport.handshake;

public class HandshakeMessageHandlerException extends Exception {
   private Throwable _cause;

   public HandshakeMessageHandlerException(Throwable cause) {
      this._cause = cause;
   }

   public Throwable getCause() {
      return this._cause;
   }
}
