package net.rim.device.api.browser.field;

public class ErrorEvent extends Event {
   private String _errorString;

   public ErrorEvent(Object src, String errorString) {
      super(10013, src);
      this._errorString = errorString;
   }

   public String getErrorString() {
      return this._errorString;
   }
}
