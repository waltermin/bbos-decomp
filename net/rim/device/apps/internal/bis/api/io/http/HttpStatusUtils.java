package net.rim.device.apps.internal.bis.api.io.http;

public final class HttpStatusUtils {
   public static final boolean isAuthenticationErrorStatus(int statusCode) {
      return statusCode == 403 || statusCode == 401 || statusCode == 407;
   }
}
