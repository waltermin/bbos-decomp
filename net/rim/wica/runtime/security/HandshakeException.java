package net.rim.wica.runtime.security;

public class HandshakeException extends Exception {
   private Throwable _cause;
   private HandshakeInfo _handshakeInfo;

   public HandshakeException(Throwable cause, HandshakeInfo info) {
      this(null, cause, info);
   }

   public HandshakeException(String message, HandshakeInfo info) {
      this(message, null, info);
   }

   public HandshakeException(String message, Throwable cause, HandshakeInfo info) {
      super(message);
      this._cause = cause;
      this._handshakeInfo = info;
   }

   public Throwable getCause() {
      return this._cause;
   }

   public HandshakeInfo getHandshakeInfo() {
      return this._handshakeInfo;
   }
}
