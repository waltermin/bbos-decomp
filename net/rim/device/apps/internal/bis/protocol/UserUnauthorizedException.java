package net.rim.device.apps.internal.bis.protocol;

public final class UserUnauthorizedException extends HttpCommException {
   public UserUnauthorizedException(int httpStatusCode) {
      super(httpStatusCode);
   }
}
