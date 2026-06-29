package net.rim.device.apps.internal.bis.protocol;

public class HttpCommException extends Exception {
   private int _httpStatusCode;

   public HttpCommException(int httpStatusCode) {
      this._httpStatusCode = httpStatusCode;
   }

   @Override
   public String toString() {
      return ((StringBuffer)(new Object())).append(super.toString()).append(":").append(this._httpStatusCode).toString();
   }
}
