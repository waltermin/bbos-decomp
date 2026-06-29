package net.rim.wica.common.debug.session;

public final class RequestUnsuccessfulException extends Exception {
   public static final int TIMEOUT_WAITING_FOR_RESPONSE;

   public RequestUnsuccessfulException(int reason) {
   }

   public RequestUnsuccessfulException(int reason, String message) {
      super(message);
   }
}
