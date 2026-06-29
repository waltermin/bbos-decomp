package net.rim.device.apps.internal.browser.cookie;

public final class CookieException extends Exception {
   private int _code;

   public CookieException(int code) {
      this._code = code;
   }

   public final int getCode() {
      return this._code;
   }
}
